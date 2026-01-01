package com.tracker.mediatracker.repo;

import com.tracker.mediatracker.model.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class MediaSpecifications {

    public static Specification<MediaItem> withFilters(String typeFilter, String statusFilter, String query) {
        return (root, query1, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(typeFilter) && !"ALL".equals(typeFilter)) {
                if ("MOVIE".equals(typeFilter)) {
                    predicates.add(cb.equal(root.type(), Movie.class));
                } else if ("SERIES".equals(typeFilter)) {
                    predicates.add(cb.equal(root.type(), Series.class));
                } else {
                    try {
                        SeriesType seriesType = SeriesType.valueOf(typeFilter);
                        predicates.add(cb.equal(cb.treat(root, Series.class).get("seriesType"), seriesType));
                    } catch (IllegalArgumentException ignored) {
                    }
                }
            }

            if (StringUtils.hasText(statusFilter) && !"ALL".equals(statusFilter)) {
                try {
                    WatchStatus status = WatchStatus.valueOf(statusFilter);
                    predicates.add(cb.equal(root.get("status"), status));
                } catch (IllegalArgumentException ignored) {
                }
            }

            if (StringUtils.hasText(query)) {
                String search = "%" + query.toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get("title")), search);
                Predicate directorLike = cb.like(cb.lower(root.get("directors")), search);
                predicates.add(cb.or(titleLike, directorLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}