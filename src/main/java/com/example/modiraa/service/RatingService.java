package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.enums.RatingType;
import com.example.modiraa.exception.CustomException;
import com.example.modiraa.exception.ErrorCode;
import com.example.modiraa.exception.NotFoundCustomException;
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

    private static final String SUCCESS_RATE_MESSAGE = " 성공";
    private static final String SUCCESS_CANCEL_MESSAGE = " 취소 성공";

    public ResponseEntity<String> rate(UserDetailsImpl userDetails, Long userId, String ratingType) {
        RatingType type = RatingType.fromValue(ratingType);
        Member giver = userDetails.getMember();
        Member receiver = getReceiver(userId);

        checkSelfRating(giver, receiver);
        checkDuplicateRating(giver, receiver);
        saveRating(type, giver, receiver);

        String successMessage = type + SUCCESS_RATE_MESSAGE;
        return ResponseEntity.status(HttpStatus.CREATED).body(successMessage);
    }

    public ResponseEntity<String> deleteRating(UserDetailsImpl userDetails, Long userId, String ratingType) {
        RatingType type = RatingType.fromValue(ratingType);
        Member giver = userDetails.getMember();
        Member receiver = getReceiver(userId);

        Rating foundRating = getFoundRating(type, giver, receiver);
        ratingRepository.delete(foundRating);

        String successMessage = type + SUCCESS_CANCEL_MESSAGE;
        return ResponseEntity.status(HttpStatus.OK).body(successMessage);
    }

    private Member getReceiver(Long userId) {
        return memberRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void checkSelfRating(Member giver, Member receiver) {
        if (giver.getId().equals(receiver.getId())) {
            throw new CustomException(ErrorCode.SELF_RATING_NOT_ALLOWED);
        }
    }

    private void checkDuplicateRating(Member giver, Member receiver) {
        Optional<Rating> foundRing = ratingRepository.findByGiverAndReceiver(giver, receiver);

        if (foundRing.isPresent()) {
            throw new CustomException(ErrorCode.DUPLICATE_RATING);
        }
    }

    private void saveRating(RatingType ratingType, Member giver, Member receiver) {
        Rating like = new Rating(ratingType, giver, receiver);
        ratingRepository.save(like);
    }

    private Rating getFoundRating(RatingType ratingType, Member giver, Member receiver) {
        return ratingRepository.findByRatingTypeAndGiverAndReceiver(ratingType, giver, receiver)
                .orElseThrow(() -> new NotFoundCustomException(ErrorCode.RATING_NOT_FOUND, ratingType.getValue()));
    }
}
