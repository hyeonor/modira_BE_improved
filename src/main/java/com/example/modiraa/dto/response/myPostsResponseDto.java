package com.example.modiraa.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class myPostsResponseDto {

    private Long postId;
    private String title;
    private String menuForImage;
    private String menu;
}
