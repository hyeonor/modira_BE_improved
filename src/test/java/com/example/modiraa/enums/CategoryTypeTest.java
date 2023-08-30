package com.example.modiraa.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTypeTest {

    @Test
    @DisplayName("CategoryType Enum fromValue() Test")
    void testFromValue() {
        System.out.println("CategoryType.GOLDEN_BELL = " + CategoryType.GOLDEN_BELL);
        System.out.println("CategoryType.DUTCH_PAY = " + CategoryType.DUTCH_PAY);

        assertThat(CategoryType.fromValue("방장이 쏜다! 골든벨")).isEqualTo(CategoryType.GOLDEN_BELL);
        assertThat(CategoryType.fromValue("다같이 내자! N빵")).isEqualTo(CategoryType.DUTCH_PAY);
    }

    @Test
    @DisplayName("CategoryType Enum getValue() Test")
    void testGetValue() {
        assertThat(CategoryType.GOLDEN_BELL.getValue()).isEqualTo("방장이 쏜다! 골든벨");
        assertThat(CategoryType.DUTCH_PAY.getValue()).isEqualTo("다같이 내자! N빵");
    }

}