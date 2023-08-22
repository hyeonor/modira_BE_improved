package com.example.modiraa.repository;

import com.example.modiraa.config.TestQuerydslConfig;
import com.example.modiraa.dto.request.oauth.OAuthProvider;
import com.example.modiraa.dto.response.MyPostsResponse;
import com.example.modiraa.enums.GenderType;
import com.example.modiraa.model.ChatRoom;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.Post;
import com.example.modiraa.model.PostImage;
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
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({TestQuerydslConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PostRepositoryTest {
    @Autowired
    private EntityManager em;

    @Autowired
    private PostRepository repository;


    @BeforeEach
    public void before() {
        Member member1 = new Member("profileImage1", "nickname1", 20,
                GenderType.MALE, "address1", "oAuthId1", OAuthProvider.KAKAO);
        Member member2 = new Member("profileImage2", "nickname2", 30,
                GenderType.MALE, "address2", "oAuthId2", OAuthProvider.KAKAO);
        Member member3 = new Member("profileImage3", "nickname3", 20,
                GenderType.FEMALE, "address3", "oAuthId3", OAuthProvider.NAVER);

        PostImage postImage1 = new PostImage("menu1", "imageUrl1");
        PostImage postImage2 = new PostImage("menu2", "imageUrl2");

        ChatRoom chatRoom1 = new ChatRoom(10);
        ChatRoom chatRoom2 = new ChatRoom(20);
        ChatRoom chatRoom3 = new ChatRoom(30);

        Post post1 = new Post("골든벨", "title1", "contents1", "서울 성동구", 1.1, 1.1,
                LocalDate.now(), LocalTime.now(), GenderType.MALE, 10, 20, member1, postImage1, chatRoom1);
        Post post2 = new Post("N빵", "title2", "contents2", "서울 용산구", 2.2, 2.2,
                LocalDate.now(), LocalTime.now(), GenderType.MALE, 20, 30, member1, postImage2, chatRoom2);
        Post post3 = new Post("N빵", "title3", "contents3", "서울 동대문구", 3.3, 3.3,
                LocalDate.now(), LocalTime.now(), GenderType.FEMALE, 30, 40, member3, postImage2, chatRoom3);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(postImage1);
        em.persist(postImage2);
        em.persist(chatRoom1);
        em.persist(chatRoom2);
        em.persist(chatRoom3);
        em.persist(post1);
        em.persist(post2);
        em.persist(post3);
    }

    @AfterEach
    public void cleanupTestData() {
        em.clear();
    }


    @Test
    @DisplayName("Post Save")
    public void testFestPostSave() {
        // Given
        Member member1 = em.find(Member.class, 1L);
        PostImage postImage1 = em.find(PostImage.class, 1L);
        ChatRoom chatRoom1 = em.find(ChatRoom.class, 1L);
        Post post1 = new Post("category1", "title1", "contents1", "address1", 1.1, 1.1,
                LocalDate.now(), LocalTime.now(), GenderType.MALE, 10, 20, member1, postImage1, chatRoom1);

        // When
        repository.save(post1);

        // Then
        Post result = repository.findById(post1.getId()).get();
        assertThat(result).isEqualTo(post1);
    }


    @Test
    @DisplayName("ChatRoom Id로 Post 조회")
    public void testFindByChatRoomId() {
        // Given
        Member member1 = em.find(Member.class, 1L);
        PostImage postImage1 = em.find(PostImage.class, 1L);
        ChatRoom chatRoom1 = em.find(ChatRoom.class, 1L);
        Post post1 = em.find(Post.class, 1L);

        Long chatRoomId = chatRoom1.getId();

        // When
        Post result = repository.findByChatRoomId(chatRoomId).get();

        // Then
        assertThat(result).isEqualTo(post1);
        assertThat(result.getId()).isEqualTo(post1.getId());
        assertThat(result.getOwner().getId()).isEqualTo(member1.getId());
        assertThat(result.getChatRoom().getId()).isEqualTo(chatRoom1.getId());
        assertThat(result.getPostImage().getId()).isEqualTo(postImage1.getId());
    }

    @Test
    @DisplayName("검색어에 따른 Post 조회 - 검색어[메뉴]")
    public void testFindBySearchKeywordAndAddress_KeywordMenu() {
        // Given
        Long lastId = 100L;
        String address = "서울";
        String keywordMenu = "menu1";
        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "id"));

        // When
        Page<Post> resultMenu = repository.findBySearchKeywordAndAddress(lastId, address, keywordMenu, pageable);

        // Then
        assertThat(resultMenu.getSize()).isEqualTo(8);
        assertThat(resultMenu.getContent().size()).isEqualTo(1);
        assertThat(resultMenu.getContent().get(0).getPostImage().getMenu()).isEqualTo("menu1");
    }

    @Test
    @DisplayName("검색어에 따른 Post 조회 - 검색어[제목]")
    public void testFindBySearchKeywordAndAddress_KeywordTitle() {
        // Given
        Long lastId = 100L;
        String address = "서울";
        String keywordTitle = "title";
        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "id"));

        // When
        Page<Post> resultTitle = repository.findBySearchKeywordAndAddress(lastId, address, keywordTitle, pageable);

        // Then
        assertThat(resultTitle.getContent().size()).isEqualTo(3);
        assertThat(resultTitle.getContent())
                .extracting("title")
                .containsExactly("title3", "title2", "title1");
    }

    @Test
    @DisplayName("검색어에 따른 Post 조회 - 검색어[내용]")
    public void testFindBySearchKeywordAndAddress_KeywordContents() {
        // Given
        Long lastId = 100L;
        String address = "서울";
        String keywordContents = "content";
        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "id"));

        // When
        Page<Post> resultContents = repository.findBySearchKeywordAndAddress(lastId, address, keywordContents, pageable);

        // Then
        assertThat(resultContents.getContent().size()).isEqualTo(3);
        assertThat(resultContents.getContent())
                .extracting("contents")
                .containsExactly("contents3", "contents2", "contents1");
    }

    @Test
    @DisplayName("lastId 및 카테고리에 따른 Post 조회")
    public void testFindByIdLessThanAndCategory() {
        // Given
        Long lastId = 100L;
        String goldenBellCategory = "골든벨";
        String dutchPayCategory = "N빵";
        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "id"));

        // When
        Page<Post> goldenBell = repository.findByIdLessThanAndCategory(lastId, goldenBellCategory, pageable);
        Page<Post> DutchPay = repository.findByIdLessThanAndCategory(lastId, dutchPayCategory, pageable);

        // Then
        assertThat(goldenBell.getContent().size()).isEqualTo(1);
        assertThat(goldenBell.getContent())
                .extracting("title")
                .containsExactly("title1");

        assertThat(DutchPay.getContent().size()).isEqualTo(2);
        assertThat(DutchPay.getContent())
                .extracting("title")
                .containsExactly("title3", "title2");
    }

    @Test
    @DisplayName("모든 Post 조회")
    public void testFindAllPosts() {
        // Given
        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "id"));

        // When
        Page<Post> result = repository.findAllPosts(pageable);

        // Then
        assertThat(result.getSize()).isEqualTo(8);

        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getContent())
                .extracting("title")
                .containsExactly("title3", "title2", "title1");
    }

    @Test
    @DisplayName("카테고리별 Post 조회")
    public void testFindByCategory() {
        // Given
        String goldenBellCategory = "골든벨";
        String dutchPayCategory = "N빵";
        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "id"));

        // When
        Page<Post> goldenBell = repository.findByCategory(goldenBellCategory, pageable);
        Page<Post> dutchPay = repository.findByCategory(dutchPayCategory, pageable);

        // Then
        assertThat(goldenBell.getSize()).isEqualTo(8);

        assertThat(goldenBell.getContent().size()).isEqualTo(1);
        assertThat(goldenBell.getContent())
                .extracting("title")
                .containsExactly("title1");

        assertThat(dutchPay.getContent().size()).isEqualTo(2);
        assertThat(dutchPay.getContent())
                .extracting("title")
                .containsExactly("title3", "title2");
    }

    @Test
    @DisplayName("로그인 회원 주소에 따른 모든 Post 조회")
    public void testFindAllByAddress() {
        // Given
        String memberAddress = "서울";
        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "id"));

        // When
        Page<Post> result = repository.findAllByAddress(memberAddress, pageable);

        // Then
        assertThat(result.getSize()).isEqualTo(8);

        assertThat(result.getContent().size()).isEqualTo(3);
        assertThat(result.getContent())
                .extracting("title")
                .containsExactly("title3", "title2", "title1");
    }

    @Test
    @DisplayName("로그인 회원 주소 및 카테고리에 따른 Post 조회")
    public void testFindByAddressAndCategory() {
        // Given
        String memberAddress = "서울";
        String goldenBellCategory = "골든벨";
        Pageable pageable = PageRequest.of(0, 8, Sort.by(Sort.Direction.DESC, "id"));

        // When
        Page<Post> result = repository.findByAddressAndCategory(memberAddress, goldenBellCategory, pageable);

        // Then
        assertThat(result.getSize()).isEqualTo(8);

        assertThat(result.getContent().size()).isEqualTo(1);
        assertThat(result.getContent())
                .extracting("title")
                .containsExactly("title1");

    }

    @Test
    @DisplayName("본인이 작성한 Post 내림차순으로 조회")
    public void testFindMyPostsByMemberOrderByDesc() {
        // Given
        Long memberId = 1L;

        // When
        List<MyPostsResponse> result = repository.findMyPostsByMemberOrderByDesc(memberId);

        // Then
        assertThat(result.size()).isEqualTo(2);
        assertThat(result)
                .extracting("title")
                .containsExactly("title2", "title1");
    }
}