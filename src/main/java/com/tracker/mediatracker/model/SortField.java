package com.tracker.mediatracker.model;

import lombok.Getter;

@Getter
public enum SortField {
    ID("id"),
    TITLE("title"),
    YEAR("releaseYear"),
    DURATION("durationMinutes"),
    DIRECTOR("directors"),
    TYPE("typeOrder"),
    STATUS("statusOrder"),
    PROGRESS("progress");

    private final String entityFieldName;

    SortField(String entityFieldName) {
        this.entityFieldName = entityFieldName;
    }

    public static SortField fromString(String param) {
        if (param == null) {
            return ID;
        }
        try {
            return SortField.valueOf(param.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ID;
        }
    }
}