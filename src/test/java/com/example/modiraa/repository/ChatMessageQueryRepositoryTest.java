package com.example.modiraa.repository;

import com.example.modiraa.config.TestQuerydslConfig;
import com.example.modiraa.dto.request.oauth.OAuthProvider;
import com.example.modiraa.enums.GenderType;
import com.example.modiraa.model.ChatMessage;
import com.example.modiraa.model.ChatRoom;
import com.example.modiraa.model.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestQuerydslConfig.class})
class ChatMessageQueryRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private ChatMessageQueryRepository repository;

    @BeforeEach
    public void before() {
        Member member1 = new Member("profileImage1", "nickname1", 20,
                GenderType.MALE, "address1", "oAuthId1", OAuthProvider.KAKAO);
        Member member2 = new Member("profileImage2", "nickname2", 30,
                GenderType.FEMALE, "address2", "oAuthId2", OAuthProvider.NAVER);

        ChatRoom chatRoom1 = new ChatRoom(5);
        ChatRoom chatRoom2 = new ChatRoom(6);

        ChatMessage chatMessage1 = new ChatMessage(ChatMessage.MessageType.ENTER,
                chatRoom1.getRoomCode(), member1, "message1");
        ChatMessage chatMessage2 = new ChatMessage(ChatMessage.MessageType.TALK,
                chatRoom1.getRoomCode(), member1, "message2");
        ChatMessage chatMessage3 = new ChatMessage(ChatMessage.MessageType.TALK,
                chatRoom1.getRoomCode(), member1, "message3");
        ChatMessage chatMessage4 = new ChatMessage(ChatMessage.MessageType.TALK,
                chatRoom2.getRoomCode(), member2, "message4");
        ChatMessage chatMessage5 = new ChatMessage(ChatMessage.MessageType.TALK,
                chatRoom2.getRoomCode(), member2, "message5");
        ChatMessage chatMessage6 = new ChatMessage(ChatMessage.MessageType.TALK,
                chatRoom2.getRoomCode(), member2, "message6");

        em.persist(member1);
        em.persist(member2);
        em.persist(chatRoom1);
        em.persist(chatRoom2);
        em.persist(chatMessage1);
        em.persist(chatMessage2);
        em.persist(chatMessage3);
        em.persist(chatMessage4);
        em.persist(chatMessage5);
        em.persist(chatMessage6);
    }

    @AfterEach
    public void cleanupTestData() {
        em.clear(); // 영속성 컨텍스트 초기화
    }

    @Test
    @DisplayName("채팅방의 최근 메세지 150개를 페이징하여 가져오기")
    void findByRoomCodeOrderByIdDesc() {
        // Given
        Pageable pageable = PageRequest.of(0, 150, Sort.by(Sort.Direction.DESC, "id"));

        ChatRoom chatRoom1 = em.find(ChatRoom.class, 1L);
        ChatRoom chatRoom2 = em.find(ChatRoom.class, 2L);

        // When
        Page<ChatMessage> result1 = repository.findByRoomCodeOrderByIdDesc(chatRoom1.getRoomCode(), pageable);
        Page<ChatMessage> result2 = repository.findByRoomCodeOrderByIdDesc(chatRoom2.getRoomCode(), pageable);

        // Then
        assertThat(result1.getContent())
                .extracting("message")
                .containsExactly("message3", "message2", "message1");
        assertThat(result2.getContent())
                .extracting("message")
                .containsExactly("message6", "message5", "message4");
    }
}