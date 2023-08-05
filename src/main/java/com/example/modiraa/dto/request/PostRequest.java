package com.example.modiraa.dto.request;

import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostRequest {
    private String category;
    private String title;
    private String contents;
    private String address;
    private double latitude;
    private double longitude;
    private String date;
    private String time;
    private int numberOfPeople;
    private String menu;
    private String gender;
    private String age;
}
