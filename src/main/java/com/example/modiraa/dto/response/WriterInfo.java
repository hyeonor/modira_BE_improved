package com.example.modiraa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriterInfo {
    private String profileImage;
    private String nickname;
    private String gender;
    private int age;
    private Long score;
}
