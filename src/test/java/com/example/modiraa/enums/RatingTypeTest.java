package com.example.modiraa.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RatingTypeTest {

    @Test
    @DisplayName("RatingType Enum fromValue() Test")
    void testFromValue() {
        System.out.println("RatingType.LIKE = " + RatingType.LIKE);
        System.out.println("RatingType.DISLIKE = " + RatingType.DISLIKE);

        assertThat(RatingType.fromValue("like")).isEqualTo(RatingType.LIKE);
        assertThat(RatingType.fromValue("dislike")).isEqualTo(RatingType.DISLIKE);
    }

    @Test
    @DisplayName("RatingTypeTest Enum getValue() Test")
    void testGetValue() {
        assertThat(RatingType.LIKE.getValue()).isEqualTo("like");
        assertThat(RatingType.DISLIKE.getValue()).isEqualTo("dislike");
    }
}