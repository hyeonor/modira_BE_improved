package com.example.modiraa.post.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostRequestDto {
    private String category;
    private String title;
    private String contents;
    private String address;
    private double latitude;
    private double longitude;
    private String date;
    private int numberOfPeople;
    private String menu;
    private String gender;
    private String age;
}
