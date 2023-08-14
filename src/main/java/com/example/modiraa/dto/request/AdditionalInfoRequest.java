package com.example.modiraa.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AdditionalInfoRequest {
    private MultipartFile modiraProfileImage;
    private String profileImage;
    private String nickname;
    private int age;
    private String gender;
    private String address;
    @JsonProperty("oAuthProvider")
    private String oAuthProvider;
    @JsonProperty("oAuthId")
    private String oAuthId;
}
