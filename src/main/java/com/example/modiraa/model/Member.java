package com.example.modiraa.model;

import com.example.modiraa.model.oauth.OAuthProvider;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String profileImage;

    @Column
    private String nickname;

    @Column
    private String username;

    @Column
    private String age;

    @Column
    private String gender;

    @Column
    private String address;

    @Column
    private String postState;

    @Column
    private Long oAuthId;

    @Column
    private OAuthProvider oAuthProvider;


    public void setPostState(String postState) {
        this.postState = postState;
    }

    public void setProfileImage(String postState) {
        this.postState = postState;
    }

    @Builder
    public Member(String profileImage, String nickname, String age, String gender, String address, Long oAuthId, OAuthProvider oAuthProvider) {
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.oAuthId = oAuthId;
        this.oAuthProvider = oAuthProvider;
    }
}
