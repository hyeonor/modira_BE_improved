package com.example.modiraa.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GenderTypeTest {

    @Test
    @DisplayName("GenderType Enum fromValue() Test")
    void testFromValue() {
        System.out.println("GenderType.ALL = " + GenderType.ALL);
        System.out.println("GenderType.MALE = " + GenderType.MALE);
        System.out.println("GenderType.FEMALE = " + GenderType.FEMALE);

        assertThat(GenderType.fromValue("모든성별")).isEqualTo(GenderType.ALL);
        assertThat(GenderType.fromValue("남성")).isEqualTo(GenderType.MALE);
        assertThat(GenderType.fromValue("여성")).isEqualTo(GenderType.FEMALE);
    }

    @Test
    @DisplayName("GenderType Enum getValue() Test")
    void testGetValue() {
        assertThat(GenderType.ALL.getValue()).isEqualTo("모든성별");
        assertThat(GenderType.MALE.getValue()).isEqualTo("남성");
        assertThat(GenderType.FEMALE.getValue()).isEqualTo("여성");
    }
}