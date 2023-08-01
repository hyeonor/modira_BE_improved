package com.example.modiraa.controller;


import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.JoinUserListResponseDto;
import com.example.modiraa.service.MemberRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class MemberRoomController {

    private final MemberRoomService memberRoomService;

    // 방 참여하기
    @PostMapping("/api/enter/{roomId}")
    public ResponseEntity<?> enterRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String roomId) {
        return memberRoomService.enterRoom(userDetails, roomId);
    }

    // 방 나가기
    @PostMapping("/api/leave/{roomId}")
    public ResponseEntity<?> leaveRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String roomId) {
        return memberRoomService.leaveRoom(userDetails, roomId);
    }

    // 참여한 유저 정보 리스트
    @GetMapping("/api/userlist/{roomId}")
    public ResponseEntity<List<JoinUserListResponseDto>> ReadMember(@PathVariable String roomId) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(memberRoomService.ReadMember(roomId));
    }
}
