package com.example.modiraa.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

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
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate date;
    @JsonFormat(pattern = "a h시 m분")
    private LocalTime time;
    private int maxParticipant;
    private String menu;
    private String gender;
    private String age;
}
