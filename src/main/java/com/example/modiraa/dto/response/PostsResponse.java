package com.example.modiraa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostsResponse {
    private Long postId;
    private String category;
    private String title;
    private String date;
    private String time;
    private String menu;
    private String gender;
    private String age;
    private String menuForImage;
    private int maxParticipant;
    private int currentParticipant;
}
