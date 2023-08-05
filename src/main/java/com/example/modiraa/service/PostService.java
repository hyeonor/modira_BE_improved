package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.PostRequestDto;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.*;
import com.example.modiraa.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRoomRepository memberRoomRepository;
    private final MemberRoomQueryRepository memberRoomQueryRepository;

    // 모임 생성
    public void createPost(PostRequestDto postRequestDto, UserDetailsImpl userDetails) {
        Member member = memberRepository.findByNickname(userDetails.getMember().getNickname())
                .orElseThrow(() -> new UsernameNotFoundException("다시 로그인해 주세요."));

        PostImage postImage = postImageRepository.findByMenu(postRequestDto.getMenu());

        if (member.getPostState() == null) {
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
                    .build();

            postRepository.save(post);

            member.setPostState(postRequestDto.getTitle());
            memberRepository.save(member);

            ChatRoom chatRoom = new ChatRoom(userDetails.getMember(), post, post.getNumberofpeople());
            chatRoomRepository.save(chatRoom);

            post.updateRoom(chatRoom);

            MemberRoom memberRoom = new MemberRoom(userDetails.getMember(), chatRoom);
            memberRoomRepository.save(memberRoom);
        } else {
            throw new CustomException(ErrorCode.POST_CHECK_CODE);
        }
    }

    // 모임 삭제
    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        List<MemberRoom> memberRoomList = memberRoomQueryRepository.findByChatRoomId(post.getChatRoom().getId());

        for (MemberRoom memberRoom : memberRoomList) {
            memberRoomRepository.deleteById(memberRoom.getId());

            Member member = memberRepository.findAllById(memberRoom.getMember().getId());

            member.setPostState(null);
            memberRepository.save(member);
        }

        Long memberId = userDetails.getMember().getId();

        if (memberId.equals(post.getMember().getId())) {
            postRepository.delete(post);
        } else {
            throw new IllegalArgumentException("모임을 삭제할 권한이 없습니다");
        }
    }

}
