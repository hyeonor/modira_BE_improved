package com.example.modiraa.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Setter
@Getter
public class PostListDto {
    private Page<PostsResponse> postAll;
    private Page<PostsResponse> postGoldenBell;
    private Page<PostsResponse> postDutchPay;
}
