package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.request.PostRequest;
import com.example.modiraa.enums.GenderType;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.*;
import com.example.modiraa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberRoomRepository memberRoomRepository;
    private final MemberRoomQueryRepository memberRoomQueryRepository;

    // 모임 생성
    public void createPost(PostRequest postRequest, UserDetailsImpl userDetails) {
        Member member = memberRepository.findByNickname(userDetails.getMember().getNickname())
                .orElseThrow(() -> new UsernameNotFoundException("다시 로그인해 주세요."));

        PostImage postImage = postImageRepository.findByMenu(postRequest.getMenu());

        checkPostStatus(member);

        ChatRoom chatRoom = new ChatRoom(userDetails.getMember(), postRequest.getNumOfPeople());
        chatRoomRepository.save(chatRoom);

        savePost(postRequest, member, postImage, chatRoom);
        updateMemberPostStatus(member, postRequest.getTitle());
        saveMemberRoom(userDetails, chatRoom);
    }

    // 모임 삭제
    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Long memberId = userDetails.getMember().getId();
        Long chatRoomId = post.getChatRoom().getId();

        checkPostDeletionPermission(post, memberId);

        List<MemberRoom> memberRoomList = memberRoomQueryRepository.findByChatRoomId(chatRoomId);

        for (MemberRoom memberRoom : memberRoomList) {
            memberRoomRepository.deleteById(memberRoom.getId());

            Member member = memberRepository.findById(memberRoom.getMember().getId())
                    .orElseThrow(() -> new IllegalArgumentException("회원이 아닙니다."));

            updateMemberPostStatus(member, null);
        }

        postRepository.delete(post);
    }

    private void savePost(PostRequest postRequest, Member member, PostImage postImage, ChatRoom chatRoom) {
        Post post = Post.builder()
                .category(postRequest.getCategory())
                .title(postRequest.getTitle())
                .contents(postRequest.getContents())
                .address(postRequest.getAddress())
                .latitude(postRequest.getLatitude())
                .longitude(postRequest.getLongitude())
                .date(postRequest.getDate())
                .time(postRequest.getTime())
                .numOfPeople(postRequest.getNumOfPeople())
                .menu(postRequest.getMenu())
                .gender(GenderType.fromValue(postRequest.getGender()))
                .age(postRequest.getAge())
                .member(member)
                .postImage(postImage)
                .chatRoom(chatRoom)
                .build();

        postRepository.save(post);
    }

    private void checkPostStatus(Member member) {
        if (member.getPostStatus() != null) {
            throw new CustomException(ErrorCode.POST_CHECK_CODE);
        }
    }

    private void checkPostDeletionPermission(Post post, Long memberId) {
        if (!memberId.equals(post.getMember().getId())) {
            throw new IllegalArgumentException("모임을 삭제할 권한이 없습니다");
        }
    }

    private void updateMemberPostStatus(Member member, String postTitle) {
        member.updatePostStatus(postTitle);
        memberRepository.save(member);
    }

    private void saveMemberRoom(UserDetailsImpl userDetails, ChatRoom chatRoom) {
        MemberRoom memberRoom = new MemberRoom(userDetails.getMember(), chatRoom);
        memberRoomRepository.save(memberRoom);
    }
}
