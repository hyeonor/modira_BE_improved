package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.enums.RatingType;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikeDislikeService {
    protected final LikeRepository likeRepository;
    protected final DislikeRepository dislikeRepository;
    protected final MemberRepository memberRepository;

    public ResponseEntity<String> rate(UserDetailsImpl userDetails, Long userId, RatingType ratingType) {
        Member receiver = findMemberById(userId);
        Member giver = userDetails.getMember();

        Optional<Dislike> foundDislike = dislikeRepository.findByGiverAndReceiver(giver, receiver);
        Optional<Like> foundLike = likeRepository.findByGiverAndReceiver(giver, receiver);

        checkDuplicate(foundDislike, foundLike);
        checkSelfRating(giver, receiver);

        switch (ratingType) {
            case LIKE:
                saveLike(receiver, giver);
                return ResponseEntity.status(HttpStatus.CREATED).body("좋아요 성공");
            case DISLIKE:
                saveDislike(giver, receiver);
                return ResponseEntity.status(HttpStatus.CREATED).body("싫어요 성공");
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 평가 유형입니다.");
        }
    }

    public ResponseEntity<String> deleteRating(UserDetailsImpl userDetails, Long userId, RatingType ratingType) {
        Member receiver = findMemberById(userId);
        Member giver = userDetails.getMember();

        switch (ratingType) {
            case LIKE:
                deleteLike(receiver, giver);
                return ResponseEntity.status(HttpStatus.OK).body("좋아요 취소 성공");
            case DISLIKE:
                deleteDislike(receiver, giver);
                return ResponseEntity.status(HttpStatus.OK).body("싫어요 취소 성공");
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘못된 평가 삭제 유형입니다.");
        }
    }

    protected Member findMemberById(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다."));
    }

    protected void checkDuplicate(Optional<Dislike> foundDislike, Optional<Like> foundLike) {
        if (foundDislike.isPresent() || foundLike.isPresent()) {
            throw new IllegalArgumentException("이미 이 유저에게 좋아요 또는 싫어요를 평가했습니다.");
        }
    }

    protected void checkSelfRating(Member giver, Member receiver) {
        if (giver.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("자기 자신을 평가할 수 없습니다.");
        }
    }

    protected void saveLike(Member receiver, Member giver) {
        Like like = new Like(giver, receiver);
        likeRepository.save(like);
    }

    protected void saveDislike(Member giver, Member receiver) {
        Dislike dislike = new Dislike(giver, receiver);
        dislikeRepository.save(dislike);
    }

    protected void deleteLike(Member receiver, Member giver) {
        Like likesFound = likeRepository.findByGiverAndReceiver(giver, receiver)
                .orElseThrow(() -> new IllegalArgumentException("좋아요 한 기록이 없습니다."));

        likeRepository.delete(likesFound);
    }

    protected void deleteDislike(Member receiver, Member giver) {
        Dislike foundDislike = dislikeRepository.findByGiverAndReceiver(giver, receiver)
                .orElseThrow(() -> new IllegalArgumentException("싫어요 한 기록이 없습니다."));

        dislikeRepository.delete(foundDislike);
    }
}
