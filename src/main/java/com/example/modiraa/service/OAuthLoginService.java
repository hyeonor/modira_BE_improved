package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.config.jwt.AuthTokens;
import com.example.modiraa.config.jwt.AuthTokensGenerator;
import com.example.modiraa.dto.request.AdditionalInfoRequest;
import com.example.modiraa.dto.response.OAuthInfoResponse;
import com.example.modiraa.dto.response.SocialResponseDto;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.oauth.OAuthLoginParams;
import com.example.modiraa.model.oauth.OAuthProvider;
import com.example.modiraa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final S3Uploader s3Uploader;
    private final MemberRepository memberRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;
    private final String TOKEN_PREFIX = "Bearer ";
    private final String HEADER_STRING = "Authorization";


    public ResponseEntity<SocialResponseDto> login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        SocialResponseDto responseDto = checkIsNewMember(oAuthInfoResponse);

        if (responseDto.getId() != null) {
            Member member = findMemberByOAuthId(oAuthInfoResponse.getId());

            UserDetailsImpl userDetails = new UserDetailsImpl(member);
            setAuthentication(userDetails);

            HttpHeaders headers = createJwtTokenHeaders(userDetails);

            return ResponseEntity.ok().headers(headers).body(responseDto);
        }

        return ResponseEntity.ok().body(responseDto);
    }

    private Member findMemberByOAuthId(Long oAuthId) {
        return memberRepository.findByOAuthId(oAuthId)
                .orElseThrow(() -> new IllegalArgumentException("oAuth Id가 없습니다."));
    }

    private void setAuthentication(UserDetailsImpl userDetails) {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private HttpHeaders createJwtTokenHeaders(UserDetailsImpl userDetails) {
        //JWT 토큰 발급
        AuthTokens authTokens = authTokensGenerator.generate(userDetails.getMember().getUsername());

        // 응답 헤더에 JWT 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_STRING, TOKEN_PREFIX + authTokens.getAccessToken());
        return headers;
    }

    private SocialResponseDto checkIsNewMember(OAuthInfoResponse oAuthInfoResponse) {
        Optional<Member> member = memberRepository.findByOAuthId(oAuthInfoResponse.getId());

        return member.map(existingMember ->
                SocialResponseDto.builder()
                        .id(existingMember.getId())
                        .nickname(existingMember.getNickname())
                        .age(existingMember.getAge())
                        .gender(existingMember.getGender())
                        .profileImage(existingMember.getProfileImage())
                        .oAuthId(existingMember.getOAuthId())
                        .build()
        ).orElse(
                SocialResponseDto.builder()
                        .oAuthId(oAuthInfoResponse.getId())
                        .profileImage(oAuthInfoResponse.getProfileImage())
                        .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
                        .build()
        );
    }

    public void createNewMember(AdditionalInfoRequest request) throws IOException {
        String profileImageUrl = request.getProfileImage();

        if (request.getModiraProfileImage() != null) {
            profileImageUrl = s3Uploader.upload(request.getModiraProfileImage(), "profile");
        }

        Member member = Member.builder()
                .profileImage(profileImageUrl)
                .nickname(request.getNickname())
                .age(request.getAge())
                .gender(request.getGender())
                .address(request.getAddress())
                .oAuthProvider(OAuthProvider.fromValue(request.getOAuthProvider()))
                .oAuthId(request.getOAuthId())
                .build();

        memberRepository.save(member);
    }
}
