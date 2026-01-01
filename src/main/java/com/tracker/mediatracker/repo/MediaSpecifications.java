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
                    SeriesType seriesType = lookupEnum(SeriesType.class, typeFilter);
                    if (seriesType != null) {
                        predicates.add(cb.equal(cb.treat(root, Series.class).get(MediaItem.FIELD_TYPE), seriesType));
                    } else {
                        predicates.add(cb.disjunction());
                    }
                }
            }

            if (StringUtils.hasText(statusFilter) && !"ALL".equals(statusFilter)) {
                WatchStatus status = lookupEnum(WatchStatus.class, statusFilter);
                if (status != null) {
                    predicates.add(cb.equal(root.get(MediaItem.FIELD_STATUS), status));
                } else {
                    predicates.add(cb.disjunction());
                }
            }

            if (StringUtils.hasText(query)) {
                String search = "%" + query.toLowerCase() + "%";
                Predicate titleLike = cb.like(cb.lower(root.get(MediaItem.FIELD_TITLE)), search);
                Predicate directorLike = cb.like(cb.lower(root.get(MediaItem.FIELD_DIRECTORS)), search);
                predicates.add(cb.or(titleLike, directorLike));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T extends Enum<T>> T lookupEnum(Class<T> enumClass, String value) {
        try {
            return Enum.valueOf(enumClass, value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}