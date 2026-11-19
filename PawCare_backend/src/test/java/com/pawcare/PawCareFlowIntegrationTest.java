package com.pawcare;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.pawcare.dto.CarouselGenerateRequest;
import com.pawcare.dto.CarouselResponse;
import com.pawcare.dto.CarouselUpdateRequest;
import com.pawcare.dto.CorrectionRequest;
import com.pawcare.dto.CourseResponse;
import com.pawcare.dto.LeadRequest;
import com.pawcare.dto.ListingRequestDto;
import com.pawcare.dto.ListingResponse;
import com.pawcare.dto.LoginRequest;
import com.pawcare.dto.PageResponse;
import com.pawcare.dto.ProviderResponse;
import com.pawcare.dto.ProviderSearchCriteria;
import com.pawcare.dto.SlideDto;
import com.pawcare.exception.BadRequestException;
import com.pawcare.exception.ResourceNotFoundException;
import com.pawcare.model.Course;
import com.pawcare.model.LeadType;
import com.pawcare.model.ProviderCategory;
import com.pawcare.model.RequestStatus;
import com.pawcare.repository.CourseRepository;
import com.pawcare.service.AuthService;
import com.pawcare.service.CarouselService;
import com.pawcare.service.CorrectionService;
import com.pawcare.service.CourseService;
import com.pawcare.service.LeadService;
import com.pawcare.service.ListingService;
import com.pawcare.service.ProviderService;

/**
 * Runs the real services, repositories and SQL against an in-memory database
 * loaded with the sample data: search filters, the listing and correction
 * flows, lead capture with seat limits, and the content studio.
 */
@SpringBootTest
class PawCareFlowIntegrationTest {

    @Autowired private ProviderService providerService;
    @Autowired private ListingService listingService;
    @Autowired private CorrectionService correctionService;
    @Autowired private CourseService courseService;
    @Autowired private LeadService leadService;
    @Autowired private CarouselService carouselService;
    @Autowired private AuthService authService;
    @Autowired private CourseRepository courseRepository;

    private static ProviderSearchCriteria criteria() {
        return new ProviderSearchCriteria();
    }

    // ------------------------------------------------------------------ search

    @Test
    void filtersCombine() {
        ProviderSearchCriteria c = criteria();
        c.setCity("hyderabad"); // case-insensitive
        c.setOpen24x7(true);

        PageResponse<ProviderResponse> page = providerService.search(c);

        assertThat(page.items()).isNotEmpty()
                .allMatch(p -> p.city().equals("Hyderabad") && p.open24x7());
    }

    @Test
    void speciesFilterNeverDuplicatesAProvider() {
        ProviderSearchCriteria c = criteria();
        c.setSpecies("Dogs");
        c.setSize(48);

        List<Long> ids = providerService.search(c).items().stream().map(ProviderResponse::id).toList();

        assertThat(ids).doesNotHaveDuplicates();
        assertThat(ids).hasSizeGreaterThan(20);
    }

    @Test
    void textSearchMatchesServices() {
        ProviderSearchCriteria c = criteria();
        c.setQ("ct scan");

        assertThat(providerService.search(c).items())
                .extracting(ProviderResponse::name)
                .containsExactly("Koramangala Pet Hospital");
    }

    @Test
    void likeWildcardsAreTreatedAsText() {
        ProviderSearchCriteria c = criteria();
        c.setQ("%");
        assertThat(providerService.search(c).totalItems()).isZero();
    }

    @Test
    void sortByFeePutsCheapestFirst() {
        ProviderSearchCriteria c = criteria();
        c.setCategory(ProviderCategory.VET_CLINIC);
        c.setSort("fee");

        List<Integer> fees = providerService.search(c).items().stream().map(ProviderResponse::feeFrom).toList();
        assertThat(fees).isSorted();
    }

    @Test
    void pageSizeIsCapped() {
        ProviderSearchCriteria c = criteria();
        c.setSize(10_000);
        assertThat(providerService.search(c).size()).isEqualTo(48);
    }

    @Test
    void invalidSortAndRatingAreRejected() {
        ProviderSearchCriteria c = criteria();
        c.setSort("cheapest");
        assertThatThrownBy(() -> providerService.search(c)).isInstanceOf(BadRequestException.class);

        ProviderSearchCriteria r = criteria();
        r.setMinRating(7.0);
        assertThatThrownBy(() -> providerService.search(r)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void featuredShowsOneProviderPerCategory() {
        List<ProviderResponse> featured = providerService.featured();
        assertThat(featured).extracting(ProviderResponse::category).doesNotHaveDuplicates();
        assertThat(featured.get(0).category()).isEqualTo(ProviderCategory.VET_CLINIC);
        assertThat(featured).allMatch(ProviderResponse::verified);
    }

    @Test
    void metaListsEveryCategory() {
        assertThat(providerService.meta().categories()).hasSize(ProviderCategory.values().length);
        assertThat(providerService.meta().cities()).contains("Vijayawada", "Hyderabad");
    }

    // ---------------------------------------------------------------- listings

    private static ListingRequestDto listing(String name) {
        ListingRequestDto d = new ListingRequestDto();
        d.setBusinessName(name);
        d.setCategory(ProviderCategory.BOARDING);
        d.setCity("  new   town ");
        d.setArea("Sector 5");
        d.setAddress("Plot 9, Sector 5");
        d.setContactName("Asha");
        d.setPhone("+91 98765 43210");
        d.setEmail("Owner@Example.com");
        d.setFeeFrom(700);
        return d;
    }

    @Test
    void approvingAListingPublishesItInSearch() {
        String name = "Integration Boarding " + System.nanoTime();
        ListingResponse submitted = listingService.submit(listing(name));
        assertThat(submitted.status()).isEqualTo(RequestStatus.PENDING);
        assertThat(submitted.city()).isEqualTo("New Town");
        assertThat(submitted.email()).isEqualTo("owner@example.com");

        ListingResponse approved = listingService.updateStatus(submitted.id(), "approved");
        assertThat(approved.providerId()).isNotNull();

        ProviderSearchCriteria c = criteria();
        c.setQ(name);
        assertThat(providerService.search(c).items()).extracting(ProviderResponse::name).containsExactly(name);

        // A reviewed listing cannot be approved twice (no duplicate providers).
        assertThatThrownBy(() -> listingService.updateStatus(submitted.id(), "APPROVED"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void rejectingAListingPublishesNothing() {
        ListingResponse submitted = listingService.submit(listing("Rejected Boarding " + System.nanoTime()));
        ListingResponse rejected = listingService.updateStatus(submitted.id(), "REJECTED");
        assertThat(rejected.providerId()).isNull();
        assertThatThrownBy(() -> listingService.updateStatus(submitted.id(), "maybe"))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void correctionNeedsAnExistingProvider() {
        CorrectionRequest r = new CorrectionRequest();
        r.setProviderId(999_999L);
        r.setField("PHONE");
        r.setDetails("The number changed");
        assertThatThrownBy(() -> correctionService.submit(r)).isInstanceOf(ResourceNotFoundException.class);

        r.setProviderId(1L);
        assertThat(correctionService.submit(r).status()).isEqualTo(RequestStatus.PENDING);
    }

    // ------------------------------------------------------------------- leads

    private static LeadRequest lead(String email, Long courseId, LeadType type) {
        LeadRequest r = new LeadRequest();
        r.setName("Student");
        r.setEmail(email);
        r.setPhone("9876543210");
        r.setBackground("STUDENT");
        r.setCourseId(courseId);
        r.setType(type);
        return r;
    }

    private Course smallCourse(int seats) {
        Course c = new Course();
        c.setSlug("test-" + System.nanoTime());
        c.setTitle("Test cohort");
        c.setTagline("t");
        c.setLevel("Beginner");
        c.setAudience("BOTH");
        c.setDurationWeeks(1);
        c.setHoursPerWeek(1);
        c.setMode("Online");
        c.setPriceInr(1);
        c.setStartDate(java.time.LocalDate.now().plusDays(10));
        c.setSeatsTotal(seats);
        return courseRepository.save(c);
    }

    @Test
    void registrationTakesASeatAndBlocksRepeats() {
        Course course = smallCourse(5);
        leadService.capture(lead("Seat@Test.com", course.getId(), LeadType.REGISTRATION));

        CourseResponse after = courseService.getBySlug(course.getSlug());
        assertThat(after.seatsLeft()).isEqualTo(4);

        assertThatThrownBy(() -> leadService.capture(lead("seat@test.com", course.getId(), LeadType.REGISTRATION)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already registered");

        // An enquiry from the same person is still welcome.
        assertThat(leadService.capture(lead("seat@test.com", course.getId(), LeadType.ENQUIRY)).id()).isNotNull();
    }

    @Test
    void registrationNeedsACourse() {
        assertThatThrownBy(() -> leadService.capture(lead("x@test.com", null, LeadType.REGISTRATION)))
                .isInstanceOf(BadRequestException.class);
        assertThat(leadService.capture(lead("x@test.com", null, LeadType.ENQUIRY)).source()).isEqualTo("direct");
    }

    @Test
    void concurrentRegistrationsNeverOverbook() throws Exception {
        Course course = smallCourse(3);
        int people = 10;
        ExecutorService pool = Executors.newFixedThreadPool(people);
        CountDownLatch start = new CountDownLatch(1);
        List<Future<Boolean>> results = new ArrayList<>();

        for (int i = 0; i < people; i++) {
            String email = "p" + i + "@race.com";
            Callable<Boolean> task = () -> {
                start.await();
                try {
                    leadService.capture(lead(email, course.getId(), LeadType.REGISTRATION));
                    return true;
                } catch (BadRequestException full) {
                    return false;
                }
            };
            results.add(pool.submit(task));
        }
        start.countDown();

        int booked = 0;
        for (Future<Boolean> f : results) {
            if (f.get()) {
                booked++;
            }
        }
        pool.shutdown();

        assertThat(booked).isEqualTo(3);
        assertThat(courseService.getBySlug(course.getSlug()).seatsLeft()).isZero();
    }

    // ---------------------------------------------------------------- carousel

    @Test
    void carouselCanBeGeneratedEditedAndReloaded() {
        CarouselGenerateRequest g = new CarouselGenerateRequest();
        g.setIdea("Summer care tips for dogs in Indian cities");
        g.setNotes("Walk early: before 8 am or after 6 pm\nWater: carry a bowl on every walk");
        g.setPlatform("LINKEDIN");
        g.setTone("EXPERT");
        g.setTheme("FOREST");
        g.setSlideCount(6);

        CarouselResponse created = carouselService.generate(g);
        assertThat(created.slides()).hasSize(4);
        assertThat(created.generatedBy()).isEqualTo("TEMPLATE");

        List<SlideDto> edited = new ArrayList<>(created.slides());
        edited.set(1, new SlideDto("CONTENT", "Walk before 8 am", "Pavements heat up fast.", null, "clock"));
        edited.remove(2);

        CarouselUpdateRequest u = new CarouselUpdateRequest();
        u.setTitle("Summer walks");
        u.setTheme("MIDNIGHT");
        u.setSlides(edited);
        carouselService.update(created.id(), u);

        CarouselResponse reloaded = carouselService.getById(created.id());
        assertThat(reloaded.title()).isEqualTo("Summer walks");
        assertThat(reloaded.theme()).isEqualTo("MIDNIGHT");
        assertThat(reloaded.slides()).hasSize(3);
        assertThat(reloaded.slides().get(1).headline()).isEqualTo("Walk before 8 am");
    }

    // -------------------------------------------------------------------- auth

    @Test
    void seededAdminCanLogInAndWrongPasswordFails() {
        LoginRequest ok = new LoginRequest();
        ok.setEmail(" ADMIN@test.com ");
        ok.setPassword("Admin@123");
        assertThat(authService.login(ok).getToken()).isNotBlank();

        LoginRequest bad = new LoginRequest();
        bad.setEmail("admin@test.com");
        bad.setPassword("wrong");
        assertThatThrownBy(() -> authService.login(bad)).isInstanceOf(BadRequestException.class);
    }
}
