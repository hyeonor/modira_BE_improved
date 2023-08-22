package com.example.modiraa.repository;

import com.example.modiraa.config.TestQuerydslConfig;
import com.example.modiraa.dto.request.oauth.OAuthProvider;
import com.example.modiraa.dto.response.JoinedMembersResponse;
import com.example.modiraa.dto.response.JoinedPostsResponse;
import com.example.modiraa.enums.GenderType;
import com.example.modiraa.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestQuerydslConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberRoomRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRoomRepository repository;

    @BeforeEach
    public void before() {
        Member member1 = new Member("profileImage1", "nickname1", 20,
                GenderType.MALE, "address1", "oAuthId1", OAuthProvider.KAKAO);
        Member member2 = new Member("profileImage2", "nickname2", 30,
                GenderType.MALE, "address2", "oAuthId2", OAuthProvider.KAKAO);
        Member member3 = new Member("profileImage3", "nickname3", 20,
                GenderType.FEMALE, "address3", "oAuthId3", OAuthProvider.NAVER);
        Member member4 = new Member("profileImage4", "nickname4", 20,
                GenderType.FEMALE, "address4", "oAuthId4", OAuthProvider.NAVER);

        PostImage postImage1 = new PostImage("menu1", "imageUrl1");
        PostImage postImage2 = new PostImage("menu2", "imageUrl2");

        ChatRoom chatRoom1 = new ChatRoom(3);
        ChatRoom chatRoom2 = new ChatRoom(4);
        ChatRoom chatRoom3 = new ChatRoom(5);

        Post post1 = new Post("골든벨", "title1", "contents1", "서울 성동구", 1.1, 1.1,
                LocalDate.now(), LocalTime.now(), GenderType.MALE, 10, 20, member1, postImage1, chatRoom1);
        Post post2 = new Post("N빵", "title2", "contents2", "서울 용산구", 2.2, 2.2,
                LocalDate.now(), LocalTime.now(), GenderType.MALE, 20, 30, member2, postImage2, chatRoom2);
        Post post3 = new Post("N빵", "title3", "contents3", "서울 동대문구", 3.3, 3.3,
                LocalDate.now(), LocalTime.now(), GenderType.FEMALE, 30, 40, member3, postImage2, chatRoom3);

        MemberRoom memberRoom1 = new MemberRoom(member2, chatRoom1);
        MemberRoom memberRoom2 = new MemberRoom(member3, chatRoom1);
        MemberRoom memberRoom3 = new MemberRoom(member4, chatRoom1);
        MemberRoom memberRoom4 = new MemberRoom(member1, chatRoom2);
        MemberRoom memberRoom5 = new MemberRoom(member3, chatRoom2);
        MemberRoom memberRoom6 = new MemberRoom(member1, chatRoom3);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.persist(postImage1);
        em.persist(postImage2);
        em.persist(chatRoom1);
        em.persist(chatRoom2);
        em.persist(chatRoom3);
        em.persist(post1);
        em.persist(post2);
        em.persist(post3);
        em.persist(memberRoom1);
        em.persist(memberRoom2);
        em.persist(memberRoom3);
        em.persist(memberRoom4);
        em.persist(memberRoom5);
        em.persist(memberRoom6);
    }

    @AfterEach
    public void cleanupTestData() {
        em.clear();
    }

    @Test
    @DisplayName("ChatRoom, Member로 MemberRoom 조회")
    void testFindByChatRoomAndMember() {
        ChatRoom chatRoom1 = em.find(ChatRoom.class, 1L);
        Member member2 = em.find(Member.class, 2L);
        MemberRoom memberRoom1 = em.find(MemberRoom.class, 1L);

        // When
        MemberRoom result = repository.findByChatRoomAndMember(chatRoom1, member2).get();

        // Then
        assertThat(result).isEqualTo(memberRoom1);
    }

    @Test
    @DisplayName("ChatRoomId로 MemberRoom 조회")
    void testFindByChatRoomId() {
        // Given
        ChatRoom chatRoom1 = em.find(ChatRoom.class, 1L);
        MemberRoom memberRoom1 = em.find(MemberRoom.class, 1L);
        MemberRoom memberRoom2 = em.find(MemberRoom.class, 2L);
        MemberRoom memberRoom3 = em.find(MemberRoom.class, 3L);

        // When
        List<MemberRoom> result = repository.findByChatRoomId(chatRoom1.getId());

        // Then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result).containsExactly(memberRoom1, memberRoom2, memberRoom3);
    }

    @Test
    @DisplayName("멤버가 가장 최근에 참여한(작성한) MemberRoom 조회")
    void testFindTopByMemberOrderByIdDesc() {
        // Given
        Member member1 = em.find(Member.class, 1L);
        MemberRoom memberRoom6 = em.find(MemberRoom.class, 6L);

        // When
        MemberRoom result = repository.findTopByMemberOrderByIdDesc(member1).get();

        // Then
        assertThat(result).isEqualTo(memberRoom6);
    }

    @Test
    @DisplayName("ChatRoomId, MemberId로 MemberRoom 조회")
    void testFindByChatRoomIdAndMemberId() {
        // Given
        ChatRoom chatRoom1 = em.find(ChatRoom.class, 1L);
        Member member2 = em.find(Member.class, 2L);
        MemberRoom memberRoom1 = em.find(MemberRoom.class, 1L);

        // When
        MemberRoom result = repository.findByChatRoomIdAndMemberId(chatRoom1.getId(), member2.getId()).get();

        // Then
        assertThat(result).isEqualTo(memberRoom1);
    }

    @Test
    @DisplayName("멤버가 참여한 모임 리스트 조회")
    void testFindJoinedPostsByMember() {
        // Given
        Member member1 = em.find(Member.class, 1L);

        // When
        List<JoinedPostsResponse> result = repository.findJoinedPostsByMember(member1.getId());

        // Then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
                .extracting("title")
                .containsExactly("title3", "title2");
    }

    @Test
    @DisplayName("참여한 멤버 정보 리스트 조회")
    void testFindJoinedMembersByMemberRoom() {
        // Given
        ChatRoom chatRoom1 = em.find(ChatRoom.class, 1L);

        // When
        List<JoinedMembersResponse> result = repository.findJoinedMembersByMemberRoom(chatRoom1.getId());

        // Then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result)
                .extracting("nickname")
                .containsExactly("nickname2", "nickname3", "nickname4");
    }
}