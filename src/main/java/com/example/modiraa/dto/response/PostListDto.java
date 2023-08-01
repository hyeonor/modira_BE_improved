package com.example.modiraa.dto.response;

import com.example.modiraa.dto.response.PostsResponseDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Setter
@Getter
public class PostListDto {
    private Page<PostsResponseDto> postAll;
    private Page<PostsResponseDto> postGoldenBell;
    private Page<PostsResponseDto> postDutchPay;
}
