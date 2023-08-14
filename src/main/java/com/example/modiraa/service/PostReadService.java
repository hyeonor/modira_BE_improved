package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.*;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.Post;
import com.example.modiraa.repository.MemberRoomRepository;
import com.example.modiraa.repository.PostRepository;
import com.example.modiraa.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostReadService {
    private final PostRepository postRepository;
    private final RatingRepository ratingRepository;
    private final MemberRoomRepository memberRoomRepository;

    // 모임 검색
    public Page<PostsResponse> searchPosts(String keyword, String address, Pageable pageable, Long lastId) {
        log.info("keyword -> {}", keyword);
        log.info("address -> {}", address);
        log.info("pageable -> {}", pageable);
        log.info("lastId -> {}", lastId);

        Page<Post> posts = postRepository.findBySearchKeywordAndAddress(lastId, address, keyword, pageable);

        log.info("result=> {}", posts);
        log.info("result=> {}", posts.getContent());

        return postResponseDto(posts);
    }

    // 카테고리별 모임 더보기
    public Page<PostsResponse> showPosts(String category, Pageable pageable, Long lastId) {
        log.info("category -> {}", category);
        log.info("lastId -> {}", lastId);

        Page<Post> posts = postRepository.findByIdLessThanAndCategory(lastId, category, pageable);

        log.info("result=> {}", posts);
        log.info("result=> {}", posts.getContent());

        return postResponseDto(posts);
    }

    // 메인 페이지 카테고리별 모임
    public PostListDto showPostList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(0, 8, sort);

        Page<Post> postAll = postRepository.findAllPosts(pageable);
        Page<Post> postGoldenBell = postRepository.findByCategory("방장이 쏜다! 골든벨", pageable);
        Page<Post> postDutchPay = postRepository.findByCategory("다같이 내자! N빵", pageable);

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
        Page<Post> postGoldenBell = postRepository.findByAddressAndCategory(memberAddress, "방장이 쏜다! 골든벨", pageable);
        Page<Post> postDutchPay = postRepository.findByAddressAndCategory(memberAddress, "다같이 내자! N빵", pageable);

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
                        .category(p.getCategory())
                        .date(p.getDate())
                        .time(p.getTime())
                        .numberOfPeople(p.getNumOfPeople())
                        .numberOfParticipant(p.getChatRoom().getCurrentPeople())
                        .menu(p.getMenu())
                        .gender(p.getGender().getValue())
                        .age(p.getAgeMin() + "대~" + p.getAgeMax() + "대")
                        .menuForImage(p.getPostImage().getImageUrl())
                        .build()
        );
    }

    // 모임 상세페이지
    public PostDetailResponse getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));

        Member member = post.getMember();
        Long score = ratingRepository.calculateScore(member.getId());

        return PostDetailResponse.builder()
                .category(post.getCategory())
                .title(post.getTitle())
                .contents(post.getContents())
                .restaurantAddress(post.getAddress())
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .date(post.getDate().split("/")[0] + " / " + post.getDate().split("/")[1] + " / " + post.getDate().split("/")[2])
                .time(post.getTime())
                .numberOfPeople(post.getNumOfPeople())
                .menu(post.getMenu())
                .genderCondition(post.getGender().getValue())
                .ageCondition(post.getAgeMin() + "대~" + post.getAgeMax() + "대")
                .roomCode(post.getChatRoom().getRoomCode())
                .currentPeople(post.getChatRoom().getCurrentPeople())
                .writerInfo(WriterInfo.builder()
                        .profileImage(member.getProfileImage())
                        .nickname(member.getNickname())
                        .gender(member.getGender().getValue())
                        .age(member.getAge())
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
        return memberRoomRepository.findJoinedPostsByMember(memberId);
    }
}

