package com.example.modiraa.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginCheckResponse {
    private String username;
    private String nickname;
}