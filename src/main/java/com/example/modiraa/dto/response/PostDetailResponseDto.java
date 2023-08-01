package com.example.modiraa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class PostDetailResponseDto {
    private String category;
    private String title;
    private String contents;
    private String restaurantAddress;
    private double latitude;
    private double longitude;
    private String date;
    private String time;
    private int numberOfPeople;
    private String menu;
    private String limitGender;
    private String limitAge;
    private String writerProfileImage;
    private String writerNickname;
    private String writerGender;
    private String writerAge;
    private Long writerScore;
    private String roomId;
    private int currentPeople;
}
