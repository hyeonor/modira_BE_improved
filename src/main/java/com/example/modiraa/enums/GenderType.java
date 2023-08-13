package com.example.modiraa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum GenderType {
    MALE("남성"),
    FEMALE("여성"),
    ALL("모든성별");

    private final String value;
    private static final Map<String, GenderType> map = new HashMap<>();

    GenderType(String value) {
        this.value = value;
    }

    static {
        for (GenderType ratingType : values()) {
            map.put(ratingType.value, ratingType);
        }
    }

    @JsonCreator
    public static GenderType fromValue(String value) {
        return map.get(value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
