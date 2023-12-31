package com.example.modiraa.controller;


import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.dto.response.JoinedMembersResponse;
import com.example.modiraa.service.RoomParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RoomParticipantController {

    private final RoomParticipantService roomParticipantService;

    // 방 참여하기
    @PostMapping("/enter/{roomId}")
    public ResponseEntity<?> enterRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String roomId) {
        return roomParticipantService.enterRoom(userDetails, roomId);
    }

    // 방 나가기
    @PostMapping("/leave/{roomId}")
    public ResponseEntity<?> leaveRoom(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable String roomId) {
        return roomParticipantService.leaveRoom(userDetails, roomId);
    }

    // 참여한 유저 정보 리스트
    @GetMapping("/join/list/{roomId}")
    public ResponseEntity<List<JoinedMembersResponse>> readMember(@PathVariable String roomId) {
        return ResponseEntity.status(HttpStatus.OK).body(roomParticipantService.readMember(roomId));
    }
}
