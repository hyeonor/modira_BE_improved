package com.example.modiraa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class MyUserProfileResponseDto {
        private String nickname;
        private String userProfile;
        private String age;
        private String address;
        private String gender;
        private Long score;
        private String isJoinPost;
        private String roomId;
}
