package com.example.modiraa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostDetailResponse {
    private String category;
    private String title;
    private String contents;
    private String restaurantAddress;
    private double latitude;
    private double longitude;
    private String date;
    private String time;
    private String menu;
    private String genderCondition;
    private String ageCondition;
    private String roomCode;
    private int maxParticipant;
    private int currentParticipant;
    private WriterInfo writerInfo;
}
