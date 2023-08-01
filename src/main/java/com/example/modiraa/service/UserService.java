package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.LoginIdCheckDto;
import com.example.modiraa.dto.SocialSignupRequestDto;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.Member;
import com.example.modiraa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;


    //소셜 사용자 회원가입
    public String registerSocialUser(SocialSignupRequestDto requestDto) throws IOException {
        String error = "";
        String username = requestDto.getUsername();
        String password = requestDto.getUsername()+"1234";
        String profileImage = requestDto.getUserProfile();
        String kakaoImage = requestDto.getKakaoImage();
        MultipartFile setProfileImage = requestDto.getUserProfileImage();
        String nickname = requestDto.getNickname();
        String age = requestDto.getAge();
        String gender = requestDto.getGender();
        String address = requestDto.getAddress();

        System.out.println(username);
        System.out.println(nickname);
        System.out.println(age);
        System.out.println(gender);
        System.out.println(address);


        Optional<Member> found = memberRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new CustomException(ErrorCode.ID_DUPLICATION_CODE);
        }

        //닉네임 중복 체크
        Optional<Member> founds = memberRepository.findByNickname(nickname);
        if (founds.isPresent()) {
            throw new CustomException(ErrorCode.NICKNAME_DUPLICATION_CODE);
        }

        // 회원가입 조건
        if (nickname.length() < 2 || nickname.length() > 8) {
            throw new CustomException(ErrorCode.LENGTH_CHECK_CODE);
        }

        // 패스워드 인코딩
        password = passwordEncoder.encode(password);
        requestDto.setPassword(password);

        // 유저 정보 저장
        Member member = new Member(profileImage, nickname, age, gender, address , null, null);

        // 프로필 이미지 추가
        if (setProfileImage != null) {
            String profileUrl = s3Uploader.upload(requestDto.getUserProfileImage(), "profile");
            member.setProfileImage(profileUrl);
        } else {
            String profileUrl = kakaoImage;
            member.setProfileImage(profileUrl);
        }

        memberRepository.save(member);

        return error;
    }


    //로그인 유저 정보 반환
    public LoginIdCheckDto userInfo(UserDetailsImpl userDetails) {
        String username = userDetails.getUsername();
        String usernickname = userDetails.getMember().getNickname();
        LoginIdCheckDto userinfo = new LoginIdCheckDto(username, usernickname);
        return userinfo;
    }

    // 유저의 닉네임으로 유저 조회
    public Member getMember(String nickname) {
        return memberRepository.findByNickname(nickname).orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));
    }
}