package com.example.modiraa.repository;

import com.example.modiraa.config.TestQuerydslConfig;
import com.example.modiraa.dto.request.oauth.OAuthProvider;
import com.example.modiraa.enums.GenderType;
import com.example.modiraa.enums.RatingType;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.Rating;
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
class RatingRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    RatingRepository repository;


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
        Member member5 = new Member("profileImage4", "nickname4", 20,
                GenderType.FEMALE, "address4", "oAuthId4", OAuthProvider.NAVER);

        Rating rating1 = new Rating(RatingType.LIKE, member2, member1);
        Rating rating2 = new Rating(RatingType.LIKE, member3, member1);
        Rating rating3 = new Rating(RatingType.LIKE, member4, member1);
        Rating rating4 = new Rating(RatingType.LIKE, member1, member2);
        Rating rating5 = new Rating(RatingType.LIKE, member3, member2);
        Rating rating6 = new Rating(RatingType.DISLIKE, member4, member2);
        Rating rating7 = new Rating(RatingType.LIKE, member1, member3);
        Rating rating8 = new Rating(RatingType.DISLIKE, member2, member3);
        Rating rating9 = new Rating(RatingType.DISLIKE, member4, member3);
        Rating rating10 = new Rating(RatingType.DISLIKE, member1, member4);
        Rating rating11 = new Rating(RatingType.DISLIKE, member2, member4);
        Rating rating12 = new Rating(RatingType.DISLIKE, member3, member4);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
        em.persist(member5);
        em.persist(rating1);
        em.persist(rating2);
        em.persist(rating3);
        em.persist(rating4);
        em.persist(rating5);
        em.persist(rating6);
        em.persist(rating7);
        em.persist(rating8);
        em.persist(rating9);
        em.persist(rating10);
        em.persist(rating11);
        em.persist(rating12);
    }

    @AfterEach
    public void cleanupTestData() {
        em.clear();
    }


    @Test
    @DisplayName("평가 하는 멤버, 평가 받는 멤버로 조회")
    void testFindByGiverAndReceiver() {
        Member member1 = em.find(Member.class, 1L);
        Member member2 = em.find(Member.class, 2L);
        Member member3 = em.find(Member.class, 3L);

        Rating rating1 = em.find(Rating.class, 1L);
        Rating rating2 = em.find(Rating.class, 2L);

        Rating result1 = repository.findByGiverAndReceiver(member2, member1).orElse(null);
        Rating result2 = repository.findByGiverAndReceiver(member3, member1).orElse(null);

        assertThat(result1).isEqualTo(rating1);
        assertThat(result2).isEqualTo(rating2);
    }

    @Test
    @DisplayName("평가 유형, 보내는 멤버, 받는 멤버로 조회")
    void testFindByRatingTypeAndGiverAndReceiver() {
        Member member1 = em.find(Member.class, 1L);
        Member member2 = em.find(Member.class, 2L);
        Member member4 = em.find(Member.class, 4L);

        Rating rating1 = em.find(Rating.class, 1L);
        Rating rating6 = em.find(Rating.class, 6L);

        Rating result1 = repository.findByRatingTypeAndGiverAndReceiver(RatingType.LIKE, member2, member1).orElse(null);
        Rating result2 = repository.findByRatingTypeAndGiverAndReceiver(RatingType.DISLIKE, member4, member2).orElse(null);

        assertThat(result1).isEqualTo(rating1);
        assertThat(result2).isEqualTo(rating6);
    }

    @Test
    @DisplayName("좋아요 받은 수 확인")
    void testCountLikesForReceiver() {
        Member member1 = em.find(Member.class, 1L);
        Member member2 = em.find(Member.class, 2L);
        Member member3 = em.find(Member.class, 3L);
        Member member4 = em.find(Member.class, 4L);
        Member member5 = em.find(Member.class, 5L);

        Long likeCount1 = repository.countLikesForReceiver(member1.getId());
        Long likeCount2 = repository.countLikesForReceiver(member2.getId());
        Long likeCount3 = repository.countLikesForReceiver(member3.getId());
        Long likeCount4 = repository.countLikesForReceiver(member4.getId());
        Long likeCount5 = repository.countLikesForReceiver(member5.getId());

        assertThat(likeCount1).isEqualTo(3);
        assertThat(likeCount2).isEqualTo(2);
        assertThat(likeCount3).isEqualTo(1);
        assertThat(likeCount4).isEqualTo(0);
        assertThat(likeCount5).isEqualTo(0);
    }

    @Test
    @DisplayName("싫어요 받은 수 확인")
    void testCountDislikesForReceiver() {
        Member member1 = em.find(Member.class, 1L);
        Member member2 = em.find(Member.class, 2L);
        Member member3 = em.find(Member.class, 3L);
        Member member4 = em.find(Member.class, 4L);
        Member member5 = em.find(Member.class, 5L);

        Long likeCount1 = repository.countDislikesForReceiver(member1.getId());
        Long likeCount2 = repository.countDislikesForReceiver(member2.getId());
        Long likeCount3 = repository.countDislikesForReceiver(member3.getId());
        Long likeCount4 = repository.countDislikesForReceiver(member4.getId());
        Long likeCount5 = repository.countDislikesForReceiver(member5.getId());

        assertThat(likeCount1).isEqualTo(0);
        assertThat(likeCount2).isEqualTo(1);
        assertThat(likeCount3).isEqualTo(2);
        assertThat(likeCount4).isEqualTo(3);
        assertThat(likeCount5).isEqualTo(0);
    }

    @Test
    @DisplayName("[좋아요 받은 수] - [싫어요 받은 수] 확인")
    void testCalculateScore() {
        Member member1 = em.find(Member.class, 1L);
        Member member2 = em.find(Member.class, 2L);
        Member member3 = em.find(Member.class, 3L);
        Member member4 = em.find(Member.class, 4L);
        Member member5 = em.find(Member.class, 5L);

        Long score1 = repository.calculateScore(member1.getId());
        Long score2 = repository.calculateScore(member2.getId());
        Long score3 = repository.calculateScore(member3.getId());
        Long score4 = repository.calculateScore(member4.getId());
        Long score5 = repository.calculateScore(member5.getId());

        assertThat(score1).isEqualTo(3);
        assertThat(score2).isEqualTo(1);
        assertThat(score3).isEqualTo(-1);
        assertThat(score4).isEqualTo(-3);
        assertThat(score5).isEqualTo(0);
    }
}