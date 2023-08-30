package com.example.modiraa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum CategoryType {
    GOLDEN_BELL("방장이 쏜다! 골든벨"),
    DUTCH_PAY("다같이 내자! N빵");

    private final String value;
    private static final Map<String, CategoryType> map = new HashMap<>();

    CategoryType(String value) {
        this.value = value;
    }

    static {
        for (CategoryType ratingType : values()) {
            map.put(ratingType.value, ratingType);
        }
    }

    @JsonCreator
    public static CategoryType fromValue(String value) {
        return map.get(value);
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
