package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.*;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.Post;
import com.example.modiraa.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostReadService {
    private final PostRepository postRepository;
    private final LikesRepository likesRepository;
    private final HatesRepository hatesRepository;
    private final PostQueryRepository postQueryRepository;

    private final MemberRoomRepository memberRoomRepository;

    // 모임 검색
    public Page<PostsResponseDto> searchPosts(String keyword, String address, Pageable pageable, Long lastId) {
        log.info("keyword -> {}", keyword);
        log.info("address -> {}", address);
        log.info("pageable -> {}", pageable);
        log.info("lastId -> {}", lastId);

        Page<Post> posts = postQueryRepository.findBySearchKeywordAndAddress(lastId, address, keyword, pageable);

        log.info("result=> {}", posts);
        log.info("result=> {}", posts.getContent());

        return postResponseDto(posts);

    }

    // 카테고리별 모임 더보기
    public Page<PostsResponseDto> showPosts(String category, Pageable pageable, Long lastId) {
        log.info("category -> {}", category);
        log.info("lastId -> {}", lastId);

        Page<Post> posts = postQueryRepository.findByIdLessThanAndCategory(lastId, category, pageable);

        log.info("result=> {}", posts);
        log.info("result=> {}", posts.getContent());

        return postResponseDto(posts);
    }

    // 메인 페이지 카테고리별 모임
    public PostListDto showPostList() {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(0, 8, sort);

        Page<Post> postAll = postQueryRepository.findAll(pageable);
        Page<Post> postGoldenBell = postQueryRepository.findByCategory("방장이 쏜다! 골든벨", pageable);
        Page<Post> postDutchPay = postQueryRepository.findByCategory("다같이 내자! N빵", pageable);

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

        Page<Post> postAll = postQueryRepository.findAllByAddress(memberAddress, pageable);
        Page<Post> postGoldenBell = postQueryRepository.findByAddressAndCategory(memberAddress, "방장이 쏜다! 골든벨", pageable);
        Page<Post> postDutchPay = postQueryRepository.findByAddressAndCategory(memberAddress,"다같이 내자! N빵", pageable);

        PostListDto postListDto = new PostListDto();

        postListDto.setPostAll(postResponseDto(postAll));
        postListDto.setPostGoldenBell(postResponseDto(postGoldenBell));
        postListDto.setPostDutchPay(postResponseDto(postDutchPay));

        return postListDto;
    }

    private Page<PostsResponseDto> postResponseDto(Page<Post> postSlice) {
        return postSlice.map(p ->
                PostsResponseDto.builder()
                        .postId(p.getId())
                        .title(p.getTitle())
                        .category(p.getCategory())
                        .date(p.getDate())
                        .time(p.getTime())
                        .numberOfPeople(p.getNumberofpeople())
                        .numberOfParticipant(p.getChatRoom().getCurrentPeople())
                        .menu(p.getMenu())
                        .gender(p.getGender())
                        .age(p.getAge())
                        .menuForImage(p.getPostImage().getImageurl())
                        .build()
        );
    }

    // 모임 상세페이지
    public PostDetailResponseDto getPostDetail(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new IllegalArgumentException("게시글이 없습니다."));

        Long score = likesRepository.likesCount(post.getMember()) - hatesRepository.hatesCount(post.getMember());

        return PostDetailResponseDto.builder()
                .category(post.getCategory())
                .title(post.getTitle())
                .contents(post.getContents())
                .restaurantAddress(post.getAddress())
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .date(post.getDate().split("/")[0] + " / " + post.getDate().split("/")[1] + " / " + post.getDate().split("/")[2])
                .time(post.getTime())
                .numberOfPeople(post.getNumberofpeople())
                .menu(post.getMenu())
                .limitGender(post.getGender())
                .limitAge(post.getAge())
                .writerProfileImage(post.getMember().getProfileImage())
                .writerNickname(post.getMember().getNickname())
                .writerGender(post.getMember().getGender())
                .writerAge(post.getMember().getAge())
                .writerScore(score)
                .roomId(post.getChatRoom().getRoomId())
                .currentPeople(post.getChatRoom().getCurrentPeople())
                .build();
    }


    //내가 쓴 참석 모임 조회
    public List<myPostsResponseDto> getMyReadPost(UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        Pageable pageable = PageRequest.ofSize(1);
        return postRepository.MyPostRead(member, pageable);
    }

    //내가 참석한 모임 조회
    public List<EnterPostsResponseDto> getMyJoinPost(UserDetailsImpl userDetails) {
        Member member = userDetails.getMember();
        Pageable pageable = PageRequest.ofSize(1);
        return memberRoomRepository.MyJoinRead(member, pageable);
    }
}

