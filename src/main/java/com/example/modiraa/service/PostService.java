package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.ChatParticipantInfo;
import com.example.modiraa.dto.request.PostRequest;
import com.example.modiraa.enums.GenderType;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.*;
import com.example.modiraa.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private static final String ALL_AGES = "모든나이";
    private static final int DEFAULT_MIN_AGE = 10;
    private static final int DEFAULT_MAX_AGE = 70;

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostImageRepository postImageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RoomParticipantRepository roomParticipantRepository;


    // 모임 생성
    public void createPost(PostRequest postRequest, UserDetailsImpl userDetails) {
        Member owner = memberRepository.findByNickname(userDetails.getMember().getNickname())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PostImage postImage = postImageRepository.findByMenu(postRequest.getMenu());

        checkPostStatus(owner);

        ChatRoom chatRoom = new ChatRoom(postRequest.getMaxParticipant());
        chatRoomRepository.save(chatRoom);

        savePost(postRequest, owner, postImage, chatRoom);
        updateMemberPostStatus(owner, postRequest.getTitle());
        saveMemberRoom(userDetails, chatRoom);
    }

    // 모임 삭제
    public void deletePost(Long postId, UserDetailsImpl userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Long memberId = userDetails.getMember().getId();
        Long chatRoomId = post.getChatRoom().getId();

        checkPostDeletionPermission(post, memberId);

        List<ChatParticipantInfo> chatParticipantInfoList = roomParticipantRepository.findByChatRoomId(chatRoomId);

        for (ChatParticipantInfo chatParticipantInfo : chatParticipantInfoList) {
            roomParticipantRepository.deleteById(chatParticipantInfo.getRoomParticipantId());

            Member member = memberRepository.findById(chatParticipantInfo.getParticipantId())
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

            updateMemberPostStatus(member, null);
        }

        postRepository.delete(post);
    }

    private void savePost(PostRequest postRequest, Member owner, PostImage postImage, ChatRoom chatRoom) {
        int ageMin = DEFAULT_MIN_AGE;
        int ageMax = DEFAULT_MAX_AGE;
        String ageRange = postRequest.getAge();

        if (!ageRange.equals(ALL_AGES)) {
            int[] ages = sortAges(ageRange);

            ageMin = ages[0];
            ageMax = ages[1];
        }

        Post post = Post.builder()
                .category(postRequest.getCategory())
                .title(postRequest.getTitle())
                .contents(postRequest.getContents())
                .address(postRequest.getAddress())
                .latitude(postRequest.getLatitude())
                .longitude(postRequest.getLongitude())
                .date(postRequest.getDate())
                .time(postRequest.getTime())
                .gender(GenderType.fromValue(postRequest.getGender()))
                .ageMin(ageMin)
                .ageMax(ageMax)
                .owner(owner)
                .postImage(postImage)
                .chatRoom(chatRoom)
                .build();

        postRepository.save(post);
    }

    private int[] sortAges(String ageRange) {
        String[] ageParts = ageRange.split("~");

        int[] arr = new int[2];
        arr[0] = Integer.parseInt(ageParts[0].split("대")[0]);
        arr[1] = Integer.parseInt(ageParts[1].split("대")[0]);

        Arrays.sort(arr);

        return arr;
    }

    private void checkPostStatus(Member member) {
        if (member.getPostStatus() != null) {
            throw new CustomException(ErrorCode.PARTICIPATION_EXISTENCE);
        }
    }

    private void checkPostDeletionPermission(Post post, Long memberId) {
        if (!memberId.equals(post.getOwner().getId())) {
            throw new CustomException(ErrorCode.PERMISSION_DENIED);
        }
    }

    private void updateMemberPostStatus(Member member, String postTitle) {
        member.updatePostStatus(postTitle);
        memberRepository.save(member);
    }

    private void saveMemberRoom(UserDetailsImpl userDetails, ChatRoom chatRoom) {
        RoomParticipant roomParticipant = new RoomParticipant(userDetails.getMember(), chatRoom);
        roomParticipantRepository.save(roomParticipant);
    }
}
