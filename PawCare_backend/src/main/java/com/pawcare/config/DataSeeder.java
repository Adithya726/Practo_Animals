package com.pawcare.config;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pawcare.model.Admin;
import com.pawcare.model.Course;
import com.pawcare.model.Provider;
import com.pawcare.model.ProviderCategory;
import com.pawcare.repository.AdminRepository;
import com.pawcare.repository.CourseRepository;
import com.pawcare.repository.ProviderRepository;

import lombok.RequiredArgsConstructor;

/**
 * Creates the admin account on first start, and fills an empty database with
 * sample providers and courses so the prototype can be explored straight away.
 * The providers are fictional sample data, not real businesses.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final AdminRepository adminRepository;
    private final ProviderRepository providerRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.name}")
    private String adminName;
    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;
    @Value("${app.seed-demo-data:true}")
    private boolean seedDemoData;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedAdmin();
        if (!seedDemoData) {
            return;
        }
        if (providerRepository.count() == 0) {
            providers().forEach(providerRepository::save);
            log.info("Loaded {} sample providers", providerRepository.count());
        }
        if (courseRepository.count() == 0) {
            courseRepository.saveAll(courses());
            log.info("Loaded sample courses");
        }
    }

    private void seedAdmin() {
        String email = adminEmail.trim().toLowerCase(Locale.ROOT);
        if (adminRepository.existsByEmail(email)) {
            return;
        }
        Admin admin = new Admin();
        admin.setName(adminName);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        adminRepository.save(admin);
        log.info("Created admin account {}", email);
    }

    // ---------------------------------------------------------------- providers

    private static List<Provider> providers() {
        return List.of(
                p("Pawsitive Care Veterinary Clinic", ProviderCategory.VET_CLINIC, "Hyderabad", "Jubilee Hills",
                        "Road No. 36, Jubilee Hills, Hyderabad 500033", "+91 90000 10001", 600, 4.8, 412, 9,
                        "9:00 AM - 9:00 PM", false, false, true, true,
                        List.of("Dogs", "Cats"), List.of("General consultation", "Vaccination", "Deworming", "Dental cleaning", "Microchipping"),
                        "Family-run clinic known for gentle handling of anxious pets. Fear-free certified vets and a separate cat waiting area."),
                p("Banjara Pet Hospital", ProviderCategory.HOSPITAL, "Hyderabad", "Banjara Hills",
                        "Road No. 12, Banjara Hills, Hyderabad 500034", "+91 90000 10002", 800, 4.6, 980, 15,
                        "Open 24 hours", true, true, false, true,
                        List.of("Dogs", "Cats", "Birds"), List.of("Emergency care", "Surgery", "ICU", "X-ray", "Ultrasound", "Orthopaedics"),
                        "Multi-speciality hospital with a 12-bed ICU, in-house imaging and surgeons on call through the night."),
                p("Gachibowli Paws & Claws", ProviderCategory.VET_CLINIC, "Hyderabad", "Gachibowli",
                        "Opp. DLF Cyber City, Gachibowli, Hyderabad 500032", "+91 90000 10003", 500, 4.5, 233, 5,
                        "10:00 AM - 8:00 PM", false, false, true, true,
                        List.of("Dogs", "Cats", "Rabbits"), List.of("General consultation", "Vaccination", "Grooming", "Home visit"),
                        "Popular with IT-corridor pet parents for evening slots and home visits on weekends."),
                p("Happy Tails Boarding Kondapur", ProviderCategory.BOARDING, "Hyderabad", "Kondapur",
                        "Botanical Garden Road, Kondapur, Hyderabad 500084", "+91 90000 10004", 900, 4.7, 318, 6,
                        "7:00 AM - 10:00 PM", false, false, false, true,
                        List.of("Dogs", "Cats"), List.of("Overnight boarding", "Daycare", "Live CCTV", "Grooming", "Pick-up and drop"),
                        "Cage-free boarding with separate floors for dogs and cats, and live camera access for pet parents."),
                p("PetScan Diagnostics", ProviderCategory.DIAGNOSTICS, "Hyderabad", "Madhapur",
                        "Ayyappa Society, Madhapur, Hyderabad 500081", "+91 90000 10005", 450, 4.4, 156, 4,
                        "8:00 AM - 8:00 PM", false, false, true, true,
                        List.of("Dogs", "Cats", "Birds", "Exotic"), List.of("Blood tests", "X-ray", "Ultrasound", "Home sample collection"),
                        "NABL-style lab workflows with reports on WhatsApp in under 6 hours."),
                p("RescueRide Pet Ambulance Hyderabad", ProviderCategory.AMBULANCE, "Hyderabad", "Citywide",
                        "Serves all of Hyderabad and Secunderabad", "+91 90000 10006", 700, 4.7, 204, 7,
                        "Open 24 hours", true, true, false, true,
                        List.of("Dogs", "Cats", "Livestock"), List.of("Emergency transport", "Oxygen on board", "Paravet attendant"),
                        "Air-conditioned ambulances with oxygen and a trained paravet, usually reaching you within 30 minutes."),
                p("Blue Cross Friends Hyderabad", ProviderCategory.NGO, "Hyderabad", "Road No. 35",
                        "Road No. 35, Jubilee Hills, Hyderabad 500033", "+91 90000 10007", 0, 4.9, 1210, 20,
                        "9:00 AM - 6:00 PM", false, true, false, true,
                        List.of("Dogs", "Cats", "Livestock", "Birds"), List.of("Rescue", "Adoption", "Sterilisation", "Anti-rabies drives"),
                        "Volunteer-led shelter running street-animal rescue, adoption camps and free sterilisation."),

                p("Indiranagar Animal Clinic", ProviderCategory.VET_CLINIC, "Bengaluru", "Indiranagar",
                        "12th Main, HAL 2nd Stage, Indiranagar, Bengaluru 560038", "+91 90000 20001", 700, 4.7, 655, 12,
                        "9:00 AM - 9:00 PM", false, false, false, true,
                        List.of("Dogs", "Cats"), List.of("General consultation", "Vaccination", "Dermatology", "Dental cleaning"),
                        "Known for skin and allergy care; offers nutrition plans for breeds prone to dermatitis."),
                p("Koramangala Pet Hospital", ProviderCategory.HOSPITAL, "Bengaluru", "Koramangala",
                        "80 Feet Road, Koramangala 4th Block, Bengaluru 560034", "+91 90000 20002", 900, 4.5, 1120, 18,
                        "Open 24 hours", true, true, false, true,
                        List.of("Dogs", "Cats", "Birds", "Exotic"), List.of("Emergency care", "Surgery", "ICU", "CT scan", "Oncology"),
                        "One of the few pet hospitals in the city with a CT scanner and an oncology unit."),
                p("Whitefield Vet at Home", ProviderCategory.VET_CLINIC, "Bengaluru", "Whitefield",
                        "ITPL Main Road, Whitefield, Bengaluru 560066", "+91 90000 20003", 850, 4.6, 287, 6,
                        "8:00 AM - 8:00 PM", false, false, true, true,
                        List.of("Dogs", "Cats", "Rabbits"), List.of("Home visit", "Vaccination", "Senior pet care", "Palliative care"),
                        "Home-visit first practice, ideal for senior pets and anxious cats who hate the car."),
                p("Bark Avenue Pet Resort", ProviderCategory.BOARDING, "Bengaluru", "Sarjapur Road",
                        "Off Sarjapur Road, Bengaluru 560035", "+91 90000 20004", 1200, 4.8, 402, 8,
                        "7:00 AM - 9:00 PM", false, false, false, true,
                        List.of("Dogs"), List.of("Overnight boarding", "Daycare", "Swimming pool", "Training", "Live CCTV"),
                        "Two-acre resort with a dog pool, play groups by size and temperament, and a vet on visit daily."),
                p("Vetlab Bengaluru", ProviderCategory.DIAGNOSTICS, "Bengaluru", "HSR Layout",
                        "Sector 2, HSR Layout, Bengaluru 560102", "+91 90000 20005", 500, 4.3, 98, 3,
                        "8:00 AM - 7:00 PM", false, false, true, false,
                        List.of("Dogs", "Cats"), List.of("Blood tests", "Urinalysis", "Home sample collection"),
                        "Affordable lab packages for annual check-ups with home sample collection."),
                p("Namma Pet Ambulance", ProviderCategory.AMBULANCE, "Bengaluru", "Citywide",
                        "Serves all of Bengaluru", "+91 90000 20006", 800, 4.6, 176, 5,
                        "Open 24 hours", true, true, false, true,
                        List.of("Dogs", "Cats"), List.of("Emergency transport", "Oxygen on board", "Stretcher"),
                        "24x7 pet ambulance with stretcher and oxygen; tie-ups with four emergency hospitals."),
                p("CUPA Friends Network", ProviderCategory.NGO, "Bengaluru", "Hebbal",
                        "Hebbal, Bengaluru 560024", "+91 90000 20007", 0, 4.8, 860, 25,
                        "9:00 AM - 5:00 PM", false, true, false, true,
                        List.of("Dogs", "Cats", "Livestock"), List.of("Rescue", "Adoption", "Trauma care", "Sterilisation"),
                        "Rescue and rehabilitation network with a trauma ward for injured street animals."),

                p("Benz Circle Pet Clinic", ProviderCategory.VET_CLINIC, "Vijayawada", "Benz Circle",
                        "MG Road, near Benz Circle, Vijayawada 520010", "+91 90000 30001", 400, 4.6, 214, 10,
                        "9:30 AM - 8:30 PM", false, false, true, true,
                        List.of("Dogs", "Cats", "Birds"), List.of("General consultation", "Vaccination", "Deworming", "Home visit"),
                        "Trusted neighbourhood clinic with fair prices and Telugu, Hindi and English speaking vets."),
                p("Krishna Veterinary Hospital", ProviderCategory.HOSPITAL, "Vijayawada", "Gunadala",
                        "Eluru Road, Gunadala, Vijayawada 520004", "+91 90000 30002", 500, 4.4, 330, 16,
                        "Open 24 hours", true, true, false, true,
                        List.of("Dogs", "Cats", "Livestock"), List.of("Emergency care", "Surgery", "X-ray", "Large animal care"),
                        "Treats pets and farm animals; one of the few 24x7 options in the city."),
                p("Paws Inn Vijayawada", ProviderCategory.BOARDING, "Vijayawada", "Patamata",
                        "Patamata, Vijayawada 520010", "+91 90000 30003", 600, 4.5, 88, 3,
                        "8:00 AM - 9:00 PM", false, false, false, false,
                        List.of("Dogs", "Cats"), List.of("Overnight boarding", "Daycare", "Grooming"),
                        "Small, homely boarding with a maximum of 15 guests at a time."),
                p("Amaravati Animal Rescue", ProviderCategory.NGO, "Vijayawada", "Bhavanipuram",
                        "Bhavanipuram, Vijayawada 520012", "+91 90000 30004", 0, 4.7, 190, 8,
                        "9:00 AM - 6:00 PM", false, true, false, true,
                        List.of("Dogs", "Cats", "Livestock"), List.of("Rescue", "Adoption", "Sterilisation"),
                        "Runs weekend adoption drives and a helpline for injured animals across the city."),
                p("Guntur Pet Care Centre", ProviderCategory.VET_CLINIC, "Guntur", "Brodipet",
                        "4th Line, Brodipet, Guntur 522002", "+91 90000 30005", 350, 4.5, 140, 11,
                        "9:00 AM - 8:00 PM", false, false, true, true,
                        List.of("Dogs", "Cats"), List.of("General consultation", "Vaccination", "Surgery", "Home visit"),
                        "Experienced surgeon-led clinic with routine spay and neuter procedures."),

                p("Adyar Vet Clinic", ProviderCategory.VET_CLINIC, "Chennai", "Adyar",
                        "LB Road, Adyar, Chennai 600020", "+91 90000 40001", 600, 4.6, 520, 14,
                        "9:00 AM - 9:00 PM", false, false, false, true,
                        List.of("Dogs", "Cats", "Birds"), List.of("General consultation", "Vaccination", "Avian care", "Dental cleaning"),
                        "Has a dedicated avian vet, popular with parrot and cockatiel parents."),
                p("Anna Nagar Pet Emergency", ProviderCategory.HOSPITAL, "Chennai", "Anna Nagar",
                        "2nd Avenue, Anna Nagar, Chennai 600040", "+91 90000 40002", 900, 4.5, 610, 9,
                        "Open 24 hours", true, true, false, true,
                        List.of("Dogs", "Cats"), List.of("Emergency care", "ICU", "Surgery", "Blood bank"),
                        "24x7 emergency centre with a canine blood bank."),
                p("Chennai Pet Diagnostics", ProviderCategory.DIAGNOSTICS, "Chennai", "T. Nagar",
                        "Usman Road, T. Nagar, Chennai 600017", "+91 90000 40003", 400, 4.2, 75, 2,
                        "8:00 AM - 8:00 PM", false, false, true, false,
                        List.of("Dogs", "Cats"), List.of("Blood tests", "X-ray", "ECG", "Home sample collection"),
                        "Quick turnaround lab with cardiac screening for senior dogs."),
                p("Marina Paws Rescue", ProviderCategory.NGO, "Chennai", "Besant Nagar",
                        "Besant Nagar, Chennai 600090", "+91 90000 40004", 0, 4.8, 455, 12,
                        "8:00 AM - 6:00 PM", false, true, false, true,
                        List.of("Dogs", "Cats", "Birds"), List.of("Rescue", "Adoption", "Feeding programme"),
                        "Community rescue group running daily feeding routes and monthly adoption camps."),

                p("Bandra Pet Clinic", ProviderCategory.VET_CLINIC, "Mumbai", "Bandra West",
                        "Hill Road, Bandra West, Mumbai 400050", "+91 90000 50001", 1000, 4.7, 890, 20,
                        "10:00 AM - 9:00 PM", false, false, false, true,
                        List.of("Dogs", "Cats", "Exotic"), List.of("General consultation", "Exotic pet care", "Dermatology", "Vaccination"),
                        "Treats exotics like turtles, iguanas and hamsters alongside dogs and cats."),
                p("Powai Animal Hospital", ProviderCategory.HOSPITAL, "Mumbai", "Powai",
                        "Hiranandani Gardens, Powai, Mumbai 400076", "+91 90000 50002", 1200, 4.6, 740, 11,
                        "Open 24 hours", true, true, false, true,
                        List.of("Dogs", "Cats"), List.of("Emergency care", "Surgery", "ICU", "MRI", "Physiotherapy"),
                        "Full-service hospital with MRI and a hydrotherapy-based rehab unit."),
                p("Mumbai PetMove Ambulance", ProviderCategory.AMBULANCE, "Mumbai", "Citywide",
                        "Serves Mumbai, Thane and Navi Mumbai", "+91 90000 50003", 1000, 4.5, 260, 6,
                        "Open 24 hours", true, true, false, true,
                        List.of("Dogs", "Cats", "Birds"), List.of("Emergency transport", "Oxygen on board", "Pet relocation"),
                        "Round-the-clock ambulance that also handles planned relocations within the city."),
                p("Andheri Pet Hotel", ProviderCategory.BOARDING, "Mumbai", "Andheri East",
                        "Marol, Andheri East, Mumbai 400059", "+91 90000 50004", 1500, 4.4, 190, 5,
                        "7:00 AM - 10:00 PM", false, false, false, false,
                        List.of("Dogs", "Cats"), List.of("Overnight boarding", "Daycare", "Live CCTV", "Airport pick-up"),
                        "Air-conditioned suites close to the airport, handy for frequent travellers."),

                p("Koregaon Park Vet Studio", ProviderCategory.VET_CLINIC, "Pune", "Koregaon Park",
                        "Lane 6, Koregaon Park, Pune 411001", "+91 90000 60001", 700, 4.6, 345, 7,
                        "9:00 AM - 9:00 PM", false, false, true, true,
                        List.of("Dogs", "Cats"), List.of("General consultation", "Vaccination", "Behaviour consults", "Home visit"),
                        "Offers behaviour consultations for reactive dogs alongside routine care."),
                p("Pune Pet Emergency Centre", ProviderCategory.HOSPITAL, "Pune", "Baner",
                        "Baner Road, Pune 411045", "+91 90000 60002", 900, 4.3, 280, 4,
                        "Open 24 hours", true, true, false, false,
                        List.of("Dogs", "Cats"), List.of("Emergency care", "Surgery", "X-ray"),
                        "Newer 24x7 emergency centre on the west side of the city."),
                p("Pune Strays Collective", ProviderCategory.NGO, "Pune", "Kothrud",
                        "Kothrud, Pune 411038", "+91 90000 60003", 0, 4.7, 310, 10,
                        "9:00 AM - 6:00 PM", false, true, false, true,
                        List.of("Dogs", "Cats"), List.of("Rescue", "Adoption", "Sterilisation", "Foster network"),
                        "Large foster network that places 40+ rescued animals a month."));
    }

    @SuppressWarnings("java:S107")
    private static Provider p(String name, ProviderCategory category, String city, String area, String address,
            String phone, int fee, double rating, int reviews, int years, String hours,
            boolean open24x7, boolean emergency, boolean homeVisit, boolean verified,
            List<String> species, List<String> services, String about) {
        Provider p = new Provider();
        p.setName(name);
        p.setCategory(category);
        p.setCity(city);
        p.setArea(area);
        p.setAddress(address);
        p.setPhone(phone);
        p.setFeeFrom(fee);
        p.setRating(rating);
        p.setReviewCount(reviews);
        p.setYearsActive(years);
        p.setOpeningHours(hours);
        p.setOpen24x7(open24x7);
        p.setEmergency(emergency);
        p.setHomeVisit(homeVisit);
        p.setVerified(verified);
        p.getSpecies().addAll(species);
        p.getServices().addAll(services);
        p.setAbout(about);
        return p;
    }

    // ------------------------------------------------------------------ courses

    private static List<Course> courses() {
        // Cohorts start on upcoming Mondays, so the landing page never shows a date in the past.
        LocalDate nextMonday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        return List.of(
                course("ai-foundations", "AI Foundations for Students",
                        "Go from ChatGPT user to confident AI builder in 6 weekends.",
                        "Beginner", "STUDENT", 6, 5, "Live online, weekends", 4999, nextMonday.plusWeeks(2), 60,
                        List.of("Write prompts that give reliable, reusable results",
                                "Build a study and research assistant with Claude, ChatGPT and Gemini",
                                "Ship 3 portfolio projects you can show in interviews",
                                "Understand how LLMs work, where they fail and how to check them"),
                        List.of("ChatGPT", "Claude", "Gemini", "Perplexity", "Notion AI", "Canva")),
                course("ai-for-professionals", "AI Productivity for Young Professionals",
                        "Automate the boring half of your job and get hours back every week.",
                        "Intermediate", "PROFESSIONAL", 8, 4, "Live online, weekday evenings", 9999, nextMonday.plusWeeks(3), 40,
                        List.of("Automate reports, emails and follow-ups with n8n and Make",
                                "Build custom GPTs and Claude projects for your team",
                                "Turn spreadsheets into dashboards and insights with AI",
                                "Present an automation case study that saves real hours"),
                        List.of("ChatGPT", "Claude", "n8n", "Make", "Google Sheets", "Zapier")),
                course("ai-design-content", "AI Design & Content Studio",
                        "Create scroll-stopping carousels, reels and landing pages without a designer.",
                        "Beginner", "BOTH", 4, 5, "Hybrid: Hyderabad + online", 6999, nextMonday.plusWeeks(5), 30,
                        List.of("Design carousels and posts in Canva and Figma with AI",
                                "Build a landing page with Lovable or Framer in a day",
                                "Create a repeatable content workflow from idea to publish",
                                "Grow a personal brand with a 30-day content calendar"),
                        List.of("Canva", "Figma", "Lovable", "Midjourney", "ChatGPT", "Buffer")));
    }

    @SuppressWarnings("java:S107")
    private static Course course(String slug, String title, String tagline, String level, String audience,
            int weeks, int hoursPerWeek, String mode, int price, LocalDate start, int seats,
            List<String> outcomes, List<String> tools) {
        Course c = new Course();
        c.setSlug(slug);
        c.setTitle(title);
        c.setTagline(tagline);
        c.setLevel(level);
        c.setAudience(audience);
        c.setDurationWeeks(weeks);
        c.setHoursPerWeek(hoursPerWeek);
        c.setMode(mode);
        c.setPriceInr(price);
        c.setStartDate(start);
        c.setSeatsTotal(seats);
        c.getOutcomes().addAll(outcomes);
        c.getTools().addAll(tools);
        return c;
    }
}
