package com.example.modiraa.service;

import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.model.Member;
import com.example.modiraa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    // Redis CacheKeys
    public static final String USER_COUNT = "USER_COUNT";
    public static final String ENTER_INFO = "ENTER_INFO";
    public static final String USER_INFO = "USER_INFO";

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsUserInfo;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    private final MemberRepository memberRepository;


    // redis 에 입장정보로 sessionId 와 roomCode를 저장하고 해단 sessionId 와 토큰에서 받아온 userId를 저장함
    public void setUserEnterInfo(String sessionId, Long memberId, String roomCode) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomCode);
        hashOpsUserInfo.put(USER_INFO, sessionId, Long.toString(memberId));
    }

    // redis 에 저장했던 sessionId 로 roomCode를 리턴함
    public String getUserEnterRoomCode(String memberId) {
        return hashOpsEnterInfo.get(ENTER_INFO, memberId);
    }

    // redis 에 저장했던 sessionId 로 userId 를 얻어오고 해당 userId 로 Member 객체를 찾아 리턴함
    public Member checkSessionUser(String sessionId) {
        Long memberId = Long.parseLong(Objects.requireNonNull(hashOpsUserInfo.get(USER_INFO, sessionId)));
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    // 유저가 나갈때 redis 에 저장했던 해당 세션 / 유저의 정보를 삭제함
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
        hashOpsUserInfo.delete(USER_INFO, sessionId);
    }

    // 채팅방 유저수 조회
    public long getUserCount(String roomCode) {
        return Long.parseLong(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomCode)).orElse("0"));
    }

    // 채팅방에 입장한 유저수 +1
    public long plusUserCount(String roomCode) {
        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomCode)).orElse(0L);
    }

    // 채팅방에 입장한 유저수 -1
    public long minusUserCount(String roomCode) {
        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomCode)).filter(count -> count > 0).orElse(0L);
    }
}
