package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.config.jwt.AuthTokens;
import com.example.modiraa.config.jwt.AuthTokensGenerator;
import com.example.modiraa.config.jwt.JwtProperties;
import com.example.modiraa.dto.request.AdditionalInfoRequest;
import com.example.modiraa.dto.request.oauth.OAuthLoginParams;
import com.example.modiraa.dto.request.oauth.OAuthProvider;
import com.example.modiraa.dto.response.OAuthInfoResponse;
import com.example.modiraa.dto.response.SocialResponse;
import com.example.modiraa.enums.GenderType;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.Member;
import com.example.modiraa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final S3Uploader s3Uploader;
    private final JwtProperties jwtProperties;
    private final OAuthInfoService OAuthInfoService;
    private final MemberRepository memberRepository;
    private final AuthTokensGenerator authTokensGenerator;


    public ResponseEntity<SocialResponse> login(OAuthLoginParams params) {
        OAuthInfoResponse oAuthInfoResponse = OAuthInfoService.request(params);
        SocialResponse responseDto = checkIsNewMember(oAuthInfoResponse);

        if (responseDto.getId() != null) {
            Member member = findMemberByOAuthId(oAuthInfoResponse.getId());
            UserDetails userDetails = authenticateMember(member);

            HttpHeaders headers = createJwtTokenHeaders(userDetails);
            return ResponseEntity.ok().headers(headers).body(responseDto);
        }

        return ResponseEntity.ok().body(responseDto);
    }

    private Member findMemberByOAuthId(String oAuthId) {
        return memberRepository.findByOAuthId(oAuthId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private UserDetails authenticateMember(Member member) {
        UserDetails userDetails = new UserDetailsImpl(member);
        setAuthentication(userDetails);
        return userDetails;
    }

    private void setAuthentication(UserDetails userDetails) {
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private HttpHeaders createJwtTokenHeaders(UserDetails userDetails) {
        //JWT 토큰 발급
        AuthTokens authTokens = authTokensGenerator.generate(userDetails.getUsername());

        // 응답 헤더에 JWT 토큰 추가
        HttpHeaders headers = new HttpHeaders();
        headers.add(jwtProperties.getAccessHeader(), jwtProperties.getTokenPrefix() + authTokens.getAccessToken());
        return headers;
    }

    private SocialResponse checkIsNewMember(OAuthInfoResponse oAuthInfoResponse) {
        Optional<Member> member = memberRepository.findByOAuthId(oAuthInfoResponse.getId());

        return member.map(existingMember ->
                SocialResponse.builder()
                        .id(existingMember.getId())
                        .nickname(existingMember.getNickname())
                        .age(existingMember.getAge())
                        .gender(existingMember.getGender().getValue())
                        .profileImage(existingMember.getProfileImage())
                        .oAuthId(existingMember.getOAuthId())
                        .build()
        ).orElse(
                SocialResponse.builder()
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
                .gender(GenderType.fromValue(request.getGender()))
                .address(request.getAddress())
                .oAuthProvider(OAuthProvider.fromValue(request.getOAuthProvider()))
                .oAuthId(request.getOAuthId())
                .build();

        memberRepository.save(member);
    }
}
