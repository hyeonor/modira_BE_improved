package com.example.modiraa.service;

import com.example.modiraa.dto.PostRequestDto;
import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.ChatRoom;
import com.example.modiraa.model.Member;
import com.example.modiraa.repository.UserRepository;
import com.example.modiraa.model.Post;
import com.example.modiraa.model.PostImage;
import com.example.modiraa.repository.PostImageRepository;
import com.example.modiraa.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;

    // 모임 생성
    public void createPost(String username, PostRequestDto postRequestDto, ChatRoom chatRoom) {
        Member member = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("다시 로그인해 주세요."));

        PostImage postImage = postImageRepository.findByMenu(postRequestDto.getMenu());

        if (member.getPostState() == null){
            Post post = Post.builder()
                    .category(postRequestDto.getCategory())
                    .title(postRequestDto.getTitle())
                    .contents(postRequestDto.getContents())
                    .address(postRequestDto.getAddress())
                    .latitude(postRequestDto.getLatitude())
                    .longitude(postRequestDto.getLongitude())
                    .date(postRequestDto.getDate())
                    .time(postRequestDto.getTime())
                    .numberofpeople(postRequestDto.getNumberOfPeople())
                    .menu(postRequestDto.getMenu())
                    .gender(postRequestDto.getGender())
                    .age(postRequestDto.getAge())
                    .member(member)
                    .postImage(postImage)
                    .chatRoom(chatRoom)
                    .build();

            member.setPostState(postRequestDto.getTitle());
            userRepository.save(member);
            postRepository.save(post);

        } else {
            throw new CustomException(ErrorCode.POST_CHECK_CODE);
        }
    }

    // 모임 삭제
    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Long memberId = userDetails.getMember().getId();

        if (memberId.equals(post.getMember().getId())) {
            postRepository.delete(post);
        } else {
            throw new IllegalArgumentException("모임을 삭제할 권한이 없습니다");
        }
    }
}
