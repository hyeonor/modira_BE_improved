package com.example.modiraa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileResponse {
    private String nickname;
    private String userProfile;
    private String age;
    private String address;
    private String gender;
    private Long score;
    private String isJoinPost;
    private String roomCode;
}
