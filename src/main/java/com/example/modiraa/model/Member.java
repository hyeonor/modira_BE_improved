package com.example.modiraa.model;

import com.example.modiraa.dto.request.oauth.OAuthProvider;
import com.example.modiraa.enums.GenderType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String profileImage;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderType gender;

    @Column(nullable = false)
    private String address;

    @Column
    private String postStatus;

    @Column(nullable = false)
    private String oAuthId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthProvider oAuthProvider;


    @Builder
    public Member(String profileImage, String nickname, int age, GenderType gender, String address, String oAuthId, OAuthProvider oAuthProvider) {
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.oAuthId = oAuthId;
        this.oAuthProvider = oAuthProvider;
    }

    public void updatePostStatus(String postStatus) {
        this.postStatus = postStatus;
    }
}
