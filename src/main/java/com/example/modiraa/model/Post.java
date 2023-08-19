package com.example.modiraa.model;

import com.example.modiraa.enums.GenderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //음식 카테고리
    @Column(nullable = false)
    private String category;

    //제목
    @Column(nullable = false)
    private String title;

    //내용
    @Column(nullable = false)
    private String contents;

    //주소
    @Column(nullable = false)
    private String address;

    //위도
    @Column(nullable = false)
    private double latitude;

    //경도
    @Column(nullable = false)
    private double longitude;

    //날짜
    @Column(nullable = false)
    private String date;

    //시간
    @Column(nullable = false)
    private String time;

    //성별
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderType gender;

    //나이대
    @Column(nullable = false)
    private int ageMin;

    @Column(nullable = false)
    private int ageMax;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Member owner;

    @OneToOne
    @JoinColumn(name = "post_image_id")
    private PostImage postImage;

    @OneToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;


    @Builder
    public Post(String category, String title, String contents, String address, double latitude, double longitude, String date,
                String time, GenderType gender, int ageMin, int ageMax, Member owner, PostImage postImage, ChatRoom chatRoom) {
        this.category = category;
        this.title = title;
        this.contents = contents;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.time = time;
        this.gender = gender;
        this.ageMin = ageMin;
        this.ageMax = ageMax;
        this.owner = owner;
        this.postImage = postImage;
        this.chatRoom = chatRoom;
    }
}
