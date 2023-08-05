package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.model.Dislike;
import com.example.modiraa.model.Like;
import com.example.modiraa.model.Member;
import com.example.modiraa.repository.DislikeRepository;
import com.example.modiraa.repository.LikeRepository;
import com.example.modiraa.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DislikeService {
    private final LikeRepository likeRepository;
    private final DislikeRepository dislikeRepository;
    private final MemberRepository memberRepository;

    //유저의 평가 점수 -1점 부여하고 싶을때
    public ResponseEntity<?> dislikeClick(UserDetailsImpl userDetails, Long userId) {
        //USERID 아이디로 USER 를 찾아서 저장
        Member receiver = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저가 없습니다"));
        Member giver = userDetails.getMember();
        Optional<Dislike> hatesFound = dislikeRepository.findByGiverAndReceiver(giver, receiver);
        Optional<Like> likesFound = likeRepository.findByGiverAndReceiver(giver, receiver);
        if (hatesFound.isPresent()) {
            return new ResponseEntity<>("중복된 싫어요는 불가능합니다.", HttpStatus.BAD_REQUEST);
        }
        if (likesFound.isPresent()) {
            return new ResponseEntity<>("한 사람의 유저에 좋아요,싫어요 둘다 평가 할 수 없습니다. ", HttpStatus.BAD_REQUEST);
        }

        if (Objects.equals(giver.getId(), receiver.getId())) {
            return new ResponseEntity<>("자기 자신을 평가할 수 없습니다.  ", HttpStatus.BAD_REQUEST);
        }

        Dislike dislike = new Dislike(giver, receiver);
        dislikeRepository.save(dislike);

        return new ResponseEntity<>("싫어요 성공! ", HttpStatus.valueOf(201));
    }


    //유저의 평가를 잘못 눌렀을 취소 기능
    public ResponseEntity<?> deleteDislike(UserDetailsImpl userDetails, Long userId) {
        // USERID 로 싫어요 한 게시물들을 리스트에 담아서
        Member receiver = memberRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저가 없습니다"));
        Member giver = userDetails.getMember();
        Optional<Dislike> hatesFound = dislikeRepository.findByGiverAndReceiver(giver, receiver);
        if (hatesFound.isEmpty()) {
            return new ResponseEntity<>("싫어요 한 기록이 없습니다.", HttpStatus.BAD_REQUEST);
        }
        dislikeRepository.delete(hatesFound.get());
        return new ResponseEntity<>("싫어요 취소 성공 .", HttpStatus.valueOf(200));
    }
}
