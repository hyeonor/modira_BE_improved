package com.example.modiraa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum RatingType {
    LIKE("like"),
    DISLIKE("dislike");

    private final String value;
    private static final Map<String, RatingType> map = new HashMap<>();

    RatingType(String value) {
        this.value = value;
    }

    static {
        for (RatingType ratingType : values()) {
            map.put(ratingType.value, ratingType);
        }
    }

    @JsonCreator
    public static RatingType fromValue(String value) {
        return map.get(value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
