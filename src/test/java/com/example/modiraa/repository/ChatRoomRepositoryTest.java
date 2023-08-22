package com.example.modiraa.repository;

import com.example.modiraa.config.TestQuerydslConfig;
import com.example.modiraa.model.ChatRoom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestQuerydslConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // EntityManager 초기화
class ChatRoomRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private ChatRoomRepository repository;


    @BeforeEach
    public void before() {
        ChatRoom chatRoom1 = new ChatRoom(3);
        ChatRoom chatRoom2 = new ChatRoom(4);
        ChatRoom chatRoom3 = new ChatRoom(5);

        em.persist(chatRoom1);
        em.persist(chatRoom2);
        em.persist(chatRoom3);
    }

    @AfterEach
    public void cleanupTestData() {
        em.clear(); // 영속성 컨텍스트 초기화
    }


    @Test
    @DisplayName("RoomCode로 채팅방 조회")
    void testFindByRoomCode() {
        // Given
        ChatRoom chatRoom1 = em.find(ChatRoom.class, 1L);
        ChatRoom chatRoom2 = em.find(ChatRoom.class, 2L);
        ChatRoom chatRoom3 = em.find(ChatRoom.class, 3L);

        // When
        ChatRoom result1 = repository.findByRoomCode(chatRoom1.getRoomCode()).get();
        ChatRoom result2 = repository.findByRoomCode(chatRoom2.getRoomCode()).get();
        ChatRoom result3 = repository.findByRoomCode(chatRoom3.getRoomCode()).get();

        // Then
        assertThat(result1).isEqualTo(chatRoom1);
        assertThat(result2).isEqualTo(chatRoom2);
        assertThat(result3).isEqualTo(chatRoom3);

        assertThat(result1.getMaxParticipant()).isEqualTo(3);
        assertThat(result2.getMaxParticipant()).isEqualTo(4);
        assertThat(result3.getMaxParticipant()).isEqualTo(5);

        assertThat(result1.getRoomCode()).isEqualTo(chatRoom1.getRoomCode());
        assertThat(result2.getRoomCode()).isEqualTo(chatRoom2.getRoomCode());
        assertThat(result3.getRoomCode()).isEqualTo(chatRoom3.getRoomCode());
    }
}