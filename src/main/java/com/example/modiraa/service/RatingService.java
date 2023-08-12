package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.enums.RatingType;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.Rating;
import com.example.modiraa.repository.MemberRepository;
import com.example.modiraa.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;
    private final MemberRepository memberRepository;

    public ResponseEntity<String> rate(UserDetailsImpl userDetails, Long userId, String ratingType) {
        Member giver = userDetails.getMember();
        Member receiver = getReceiver(userId);

        checkSelfRating(giver, receiver);

        Optional<Rating> foundRing = ratingRepository.findByGiverAndReceiver(giver, receiver);

        checkDuplicateRating(foundRing);

        if (ratingType.equals(RatingType.LIKE.getValue())) {
            saveRating(RatingType.LIKE, giver, receiver);
            return ResponseEntity.status(HttpStatus.CREATED).body("좋아요 성공!");
        } else {
            saveRating(RatingType.DISLIKE, giver, receiver);
            return ResponseEntity.status(HttpStatus.CREATED).body("싫어요 성공!");
        }
    }

    public ResponseEntity<String> deleteRating(UserDetailsImpl userDetails, Long userId, String ratingType) {
        Member giver = userDetails.getMember();
        Member receiver = getReceiver(userId);

        if (ratingType.equals(RatingType.LIKE.getValue())) {
            Rating foundRating = getFoundRating(RatingType.LIKE, giver, receiver);
            ratingRepository.delete(foundRating);
            return ResponseEntity.status(HttpStatus.CREATED).body("좋아요 취소 성공.");
        } else {
            Rating foundRating = getFoundRating(RatingType.DISLIKE, giver, receiver);
            ratingRepository.delete(foundRating);
            return ResponseEntity.status(HttpStatus.OK).body("싫어요 취소 성공.");
        }
    }

    private Member getReceiver(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 없습니다"));
    }

    private void checkSelfRating(Member giver, Member receiver) {
        if (giver.getId().equals(receiver.getId())) {
            throw new IllegalArgumentException("자기 자신을 평가할 수 없습니다.");
        }
    }

    private void checkDuplicateRating(Optional<Rating> foundRing) {
        if (foundRing.isPresent()) {
            throw new IllegalArgumentException("이미 이 유저에게 좋아요 또는 싫어요를 평가했습니다.");
        }
    }

    private void saveRating(RatingType ratingType, Member giver, Member receiver) {
        Rating like = new Rating(ratingType, giver, receiver);
        ratingRepository.save(like);
    }

    private Rating getFoundRating(RatingType ratingType, Member giver, Member receiver) {
        return ratingRepository.findByRatingTypeAndGiverAndReceiver(ratingType, giver, receiver)
                .orElseThrow(() -> new IllegalArgumentException(ratingType.getValue() + "한 기록이 없습니다."));
    }
}
