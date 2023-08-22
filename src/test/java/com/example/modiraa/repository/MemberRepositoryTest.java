package com.example.modiraa.repository;

import com.example.modiraa.config.TestQuerydslConfig;
import com.example.modiraa.dto.request.oauth.OAuthProvider;
import com.example.modiraa.enums.GenderType;
import com.example.modiraa.model.Member;
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
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberRepositoryTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private MemberRepository repository;

    @BeforeEach //테스트 실행 전 데이터를 넣는 작업 진행
    public void before() {
        Member member1 = new Member("profileImage1", "nickname1", 20,
                GenderType.MALE, "address1", "oAuthId1", OAuthProvider.KAKAO);
        Member member2 = new Member("profileImage2", "nickname2", 30,
                GenderType.MALE, "address2", "oAuthId2", OAuthProvider.KAKAO);
        Member member3 = new Member("profileImage3", "nickname3", 20,
                GenderType.FEMALE, "address3", "oAuthId3", OAuthProvider.NAVER);
        Member member4 = new Member("profileImage4", "nickname4", 20,
                GenderType.FEMALE, "address4", "oAuthId4", OAuthProvider.NAVER);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }

    @AfterEach
    public void cleanupTestData() {
        em.clear();
    }

    @Test
    @DisplayName("Optional<Member> 닉네임으로 멤버 찾기")
    void findByNickname() {
        // Given
        String nickname1 = "nickname1";
        String nickname2 = "nickname2";
        String nickname3 = "nickname3";
        String nickname4 = "nickname4";

        // When
        Member result1 = repository.findByNickname(nickname1).get();
        Member result2 = repository.findByNickname(nickname2).get();
        Member result3 = repository.findByNickname(nickname3).get();
        Member result4 = repository.findByNickname(nickname4).get();

        // Then
        assertThat(result1.getNickname()).isEqualTo(nickname1);
        assertThat(result2.getNickname()).isEqualTo(nickname2);
        assertThat(result3.getNickname()).isEqualTo(nickname3);
        assertThat(result4.getNickname()).isEqualTo(nickname4);
    }

    @Test
    @DisplayName("<T> Optional<T> 닉네임으로 멤버 찾기")
    void testFindByNickname() {
        // Given
        String nickname1 = "nickname1";
        String nickname2 = "nickname2";
        String nickname3 = "nickname3";
        String nickname4 = "nickname4";

        // When
        Member result1 = repository.findByNickname(nickname1, Member.class).get();
        Member result2 = repository.findByNickname(nickname2, Member.class).get();
        Member result3 = repository.findByNickname(nickname3, Member.class).get();
        Member result4 = repository.findByNickname(nickname4, Member.class).get();

        // Then
        assertThat(result1.getNickname()).isEqualTo(nickname1);
        assertThat(result2.getNickname()).isEqualTo(nickname2);
        assertThat(result3.getNickname()).isEqualTo(nickname3);
        assertThat(result4.getNickname()).isEqualTo(nickname4);
    }

    @Test
    @DisplayName("OAuthId로 멤버 찾기")
    void testFindByOAuthId() {
        // Given
        String oAuthId1 = "oAuthId1";
        String oAuthId2 = "oAuthId2";
        String oAuthId3 = "oAuthId3";
        String oAuthId4 = "oAuthId4";

        // When
        Member result1 = repository.findByOAuthId(oAuthId1).get();
        Member result2 = repository.findByOAuthId(oAuthId2).get();
        Member result3 = repository.findByOAuthId(oAuthId3).get();
        Member result4 = repository.findByOAuthId(oAuthId4).get();

        // Then
        assertThat(result1.getOAuthId()).isEqualTo(oAuthId1);
        assertThat(result2.getOAuthId()).isEqualTo(oAuthId2);
        assertThat(result3.getOAuthId()).isEqualTo(oAuthId3);
        assertThat(result4.getOAuthId()).isEqualTo(oAuthId4);
    }
}