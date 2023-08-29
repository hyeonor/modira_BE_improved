package com.example.modiraa.repository;

import com.example.modiraa.config.TestQuerydslConfig;
import com.example.modiraa.dto.ChatParticipantInfo;
import com.example.modiraa.dto.request.oauth.OAuthProvider;
import com.example.modiraa.dto.response.JoinedMembersResponse;
import com.example.modiraa.dto.response.JoinedPostsResponse;
import com.example.modiraa.enums.CategoryType;
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
class RoomParticipantRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private RoomParticipantRepository repository;

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

        Post post1 = new Post(CategoryType.GOLDEN_BELL, "title1", "contents1", "서울 성동구", 1.1, 1.1,
                LocalDate.now(), LocalTime.now(), GenderType.MALE, 10, 20, member1, postImage1, chatRoom1);
        Post post2 = new Post(CategoryType.DUTCH_PAY, "title2", "contents2", "서울 용산구", 2.2, 2.2,
                LocalDate.now(), LocalTime.now(), GenderType.MALE, 20, 30, member2, postImage2, chatRoom2);
        Post post3 = new Post(CategoryType.DUTCH_PAY, "title3", "contents3", "서울 동대문구", 3.3, 3.3,
                LocalDate.now(), LocalTime.now(), GenderType.FEMALE, 30, 40, member3, postImage2, chatRoom3);

        RoomParticipant roomParticipant1 = new RoomParticipant(member2, chatRoom1);
        RoomParticipant roomParticipant2 = new RoomParticipant(member3, chatRoom1);
        RoomParticipant roomParticipant3 = new RoomParticipant(member4, chatRoom1);
        RoomParticipant roomParticipant4 = new RoomParticipant(member1, chatRoom2);
        RoomParticipant roomParticipant5 = new RoomParticipant(member3, chatRoom2);
        RoomParticipant roomParticipant6 = new RoomParticipant(member1, chatRoom3);

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
        em.persist(roomParticipant1);
        em.persist(roomParticipant2);
        em.persist(roomParticipant3);
        em.persist(roomParticipant4);
        em.persist(roomParticipant5);
        em.persist(roomParticipant6);
    }

    @AfterEach
    public void cleanupTestData() {
        em.clear();
    }

    @Test
    @DisplayName("ChatRoom, Member로 RoomParticipant 조회")
    void testFindByChatRoomAndMember() {
        ChatRoom chatRoom1 = em.find(ChatRoom.class, 1L);
        Member member2 = em.find(Member.class, 2L);
        RoomParticipant roomParticipant1 = em.find(RoomParticipant.class, 1L);

        // When
        RoomParticipant result = repository.findByChatRoomAndMember(chatRoom1, member2).get();

        // Then
        assertThat(result).isEqualTo(roomParticipant1);
    }

    @Test
    @DisplayName("ChatRoomId로 RoomParticipant 조회")
    void testFindByChatRoomId() {
        // Given
        ChatRoom chatRoom1 = em.find(ChatRoom.class, 1L);
        RoomParticipant roomParticipant1 = em.find(RoomParticipant.class, 1L);
        RoomParticipant roomParticipant2 = em.find(RoomParticipant.class, 2L);
        RoomParticipant roomParticipant3 = em.find(RoomParticipant.class, 3L);

        // When
        List<ChatParticipantInfo> result = repository.findByChatRoomId(chatRoom1.getId());

        // Then
        assertThat(result.size()).isEqualTo(3);
        assertThat(result)
                .extracting("roomParticipantId")
                .containsExactly(roomParticipant1.getId(), roomParticipant2.getId(), roomParticipant3.getId());
        assertThat(result)
                .extracting("participantId")
                .containsExactly(roomParticipant1.getMember().getId(), roomParticipant2.getMember().getId(), roomParticipant3.getMember().getId());

    }

    @Test
    @DisplayName("멤버가 가장 최근에 참여한(작성한) RoomParticipant 조회")
    void testFindTopByMemberOrderByIdDesc() {
        // Given
        Member member1 = em.find(Member.class, 1L);
        RoomParticipant roomParticipant6 = em.find(RoomParticipant.class, 6L);

        // When
        RoomParticipant result = repository.findTopByMemberOrderByIdDesc(member1).get();

        // Then
        assertThat(result).isEqualTo(roomParticipant6);
    }

    @Test
    @DisplayName("ChatRoomId, MemberId로 RoomParticipant 조회")
    void testFindByChatRoomIdAndMemberId() {
        // Given
        ChatRoom chatRoom1 = em.find(ChatRoom.class, 1L);
        Member member2 = em.find(Member.class, 2L);
        RoomParticipant roomParticipant1 = em.find(RoomParticipant.class, 1L);

        // When
        RoomParticipant result = repository.findByChatRoomIdAndMemberId(chatRoom1.getId(), member2.getId()).get();

        // Then
        assertThat(result).isEqualTo(roomParticipant1);
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