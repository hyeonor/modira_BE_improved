package com.example.modiraa.dto.response;

import com.example.modiraa.model.oauth.OAuthProvider;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SocialResponseDto {
    private Long id;
    private String nickname;
    private String age;
    private String gender;
    private String address;
    private String oAuthId;
    private String profileImage;
    private OAuthProvider oAuthProvider;
}
