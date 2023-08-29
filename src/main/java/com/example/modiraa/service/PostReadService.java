package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.*;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.Post;
import com.example.modiraa.repository.PostRepository;
import com.example.modiraa.repository.RatingRepository;
import com.example.modiraa.repository.RoomParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostReadService {
    private static final String CATEGORY_GOLDEN_BELL = "방장이 쏜다! 골든벨";
    private static final String CATEGORY_DUTCH_PAY = "다같이 내자! N빵";

    private final PostRepository postRepository;
    private final RatingRepository ratingRepository;
    private final RoomParticipantRepository roomParticipantRepository;

    // 모임 검색
    public Page<PostsResponse> searchPosts(String keyword, String address, Pageable pageable, Long lastId) {
        log.info("keyword -> {}", keyword);
        log.info("address -> {}", address);
        log.info("pageable -> {}", pageable);
        log.info("lastId -> {}", lastId);

        Page<Post> posts = postRepository.findBySearchKeywordAndAddress(lastId, address, keyword, pageable);

        return postResponseDto(posts);
    }

    // 카테고리별 모임 더보기
    public Page<PostsResponse> showPosts(String category, Pageable pageable, Long lastId) {
        log.info("category -> {}", category);
        log.info("lastId -> {}", lastId);

        Page<Post> posts = postRepository.findByIdLessThanAndCategory(lastId, category, pageable);

        return postResponseDto(posts);
    }

    // 메인 페이지 카테고리별 모임
    public PostListDto showPostList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(0, 8, sort);

        Page<Post> postAll = postRepository.findAllPosts(pageable);
        Page<Post> postGoldenBell = postRepository.findByCategory(CATEGORY_GOLDEN_BELL, pageable);
        Page<Post> postDutchPay = postRepository.findByCategory(CATEGORY_DUTCH_PAY, pageable);

        PostListDto postListDto = new PostListDto();

        postListDto.setPostAll(postResponseDto(postAll));
        postListDto.setPostGoldenBell(postResponseDto(postGoldenBell));
        postListDto.setPostDutchPay(postResponseDto(postDutchPay));

        return postListDto;
    }

    // 로그인 후 메인 페이지 카테고리별 모임
    public PostListDto showPostListMember(UserDetailsImpl userDetails) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(0, 8, sort);

        String memberAddress = userDetails.getMember().getAddress();

        log.info("memberAddress: {}", memberAddress);

        Page<Post> postAll = postRepository.findAllByAddress(memberAddress, pageable);
        Page<Post> postGoldenBell = postRepository.findByAddressAndCategory(memberAddress, CATEGORY_GOLDEN_BELL, pageable);
        Page<Post> postDutchPay = postRepository.findByAddressAndCategory(memberAddress, CATEGORY_DUTCH_PAY, pageable);

        PostListDto postListDto = new PostListDto();

        postListDto.setPostAll(postResponseDto(postAll));
        postListDto.setPostGoldenBell(postResponseDto(postGoldenBell));
        postListDto.setPostDutchPay(postResponseDto(postDutchPay));

        return postListDto;
    }

    private Page<PostsResponse> postResponseDto(Page<Post> postSlice) {
        return postSlice.map(p ->
                PostsResponse.builder()
                        .postId(p.getId())
                        .title(p.getTitle())
                        .category(p.getCategory().getValue())
                        .date(p.getDate().format(DateTimeFormatter.ofPattern("yyyy/MM/dd")))
                        .time(p.getTime().format(DateTimeFormatter.ofPattern("a h시 m분")))
                        .maxParticipant(p.getChatRoom().getMaxParticipant())
                        .currentParticipant(p.getChatRoom().getCurrentParticipant())
                        .menu(p.getPostImage().getMenu())
                        .gender(p.getGender().getValue())
                        .age(p.getAgeMin() + "대~" + p.getAgeMax() + "대")
                        .menuForImage(p.getPostImage().getImageUrl())
                        .build()
        );
    }

    // 모임 상세페이지
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Member owner = post.getOwner();
        Long score = ratingRepository.calculateScore(owner.getId());

        String formatDate = post.getDate().format(DateTimeFormatter.ofPattern("yyyy / MM / dd"));
        String formatTime = post.getTime().format(DateTimeFormatter.ofPattern("a h시 m분"));

        return PostDetailResponse.builder()
                .category(post.getCategory().getValue())
                .title(post.getTitle())
                .contents(post.getContents())
                .restaurantAddress(post.getAddress())
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .date(formatDate)
                .time(formatTime)
                .maxParticipant(post.getChatRoom().getMaxParticipant())
                .menu(post.getPostImage().getMenu())
                .genderCondition(post.getGender().getValue())
                .ageCondition(post.getAgeMin() + "대~" + post.getAgeMax() + "대")
                .roomCode(post.getChatRoom().getRoomCode())
                .currentParticipant(post.getChatRoom().getCurrentParticipant())
                .writerInfo(WriterInfo.builder()
                        .profileImage(owner.getProfileImage())
                        .nickname(owner.getNickname())
                        .gender(owner.getGender().getValue())
                        .age(owner.getAge())
                        .score(score)
                        .build())
                .build();
    }


    //내가 작성한 모임 조회
    public List<MyPostsResponse> getMyReadPost(UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();
        return postRepository.findMyPostsByMemberOrderByDesc(memberId);
    }

    //내가 참석한 모임 조회
    public List<JoinedPostsResponse> getMyJoinPost(UserDetailsImpl userDetails) {
        Long memberId = userDetails.getMember().getId();
        return roomParticipantRepository.findJoinedPostsByMember(memberId);
    }
}

