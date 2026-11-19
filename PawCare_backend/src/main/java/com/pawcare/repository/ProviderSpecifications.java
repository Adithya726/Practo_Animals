package com.pawcare.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

import com.pawcare.dto.ProviderSearchCriteria;
import com.pawcare.model.Provider;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Root;

/** Builds the provider search query from the optional filters, adding only the ones that are set. */
public final class ProviderSpecifications {

    private ProviderSpecifications() {
    }

    public static Specification<Provider> matching(ProviderSearchCriteria c) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (c.getCategory() != null) {
                predicates.add(cb.equal(root.get("category"), c.getCategory()));
            }
            if (hasText(c.getCity())) {
                predicates.add(cb.equal(cb.lower(root.get("city")), c.getCity().trim().toLowerCase(Locale.ROOT)));
            }
            if (Boolean.TRUE.equals(c.getOpen24x7())) {
                predicates.add(cb.isTrue(root.get("open24x7")));
            }
            if (Boolean.TRUE.equals(c.getEmergency())) {
                predicates.add(cb.isTrue(root.get("emergency")));
            }
            if (Boolean.TRUE.equals(c.getHomeVisit())) {
                predicates.add(cb.isTrue(root.get("homeVisit")));
            }
            if (Boolean.TRUE.equals(c.getVerified())) {
                predicates.add(cb.isTrue(root.get("verified")));
            }
            if (c.getMinRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("rating"), c.getMinRating()));
            }
            if (c.getMaxFee() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("feeFrom"), c.getMaxFee()));
            }

            // Collection filters use EXISTS subqueries, so a provider never repeats in the results.
            if (hasText(c.getSpecies())) {
                Subquery<Long> sq = query.subquery(Long.class);
                Root<Provider> p = sq.from(Provider.class);
                Join<Provider, String> species = p.join("species");
                sq.select(p.get("id")).where(
                        cb.equal(p.get("id"), root.get("id")),
                        cb.equal(cb.lower(species), c.getSpecies().trim().toLowerCase(Locale.ROOT)));
                predicates.add(cb.exists(sq));
            }

            if (hasText(c.getQ())) {
                String like = "%" + escape(c.getQ().trim().toLowerCase(Locale.ROOT)) + "%";

                Subquery<Long> sq = query.subquery(Long.class);
                Root<Provider> p = sq.from(Provider.class);
                Join<Provider, String> service = p.join("services");
                sq.select(p.get("id")).where(
                        cb.equal(p.get("id"), root.get("id")),
                        cb.like(cb.lower(service), like, '\\'));

                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like, '\\'),
                        cb.like(cb.lower(root.get("area")), like, '\\'),
                        cb.like(cb.lower(root.get("city")), like, '\\'),
                        cb.exists(sq)));
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

    /** Escapes LIKE wildcards so a search for "100%" matches the text, not everything. */
    static String escape(String s) {
        return s.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
