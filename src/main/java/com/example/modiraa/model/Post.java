package com.example.modiraa.model;

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

    //인원 수
    @Column(nullable = false)
    private int numOfPeople;

    //음식 메뉴
    @Column(nullable = false)
    private String menu;

    //성별
    @Column(nullable = false)
    private String gender;

    //나이대
    @Column(nullable = false)
    private String age;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne
    @JoinColumn(name = "post_image_id")
    private PostImage postImage;

    @OneToOne
    @JoinColumn(name = "chat_room_id")
    private ChatRoom chatRoom;

    public void updateRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    @Builder
    public Post(String category, String title, String contents, String address, double latitude, double longitude,
                String date, String time, int numOfPeople, String menu, String gender, String age, Member member, PostImage postImage) {
        this.category = category;
        this.title = title;
        this.contents = contents;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.time = time;
        this.numOfPeople = numOfPeople;
        this.menu = menu;
        this.gender = gender;
        this.age = age;
        this.member = member;
        this.postImage = postImage;
    }
}
