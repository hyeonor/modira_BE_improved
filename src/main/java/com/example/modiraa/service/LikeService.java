package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.enums.RatingType;
import com.example.modiraa.repository.DislikeRepository;
import com.example.modiraa.repository.LikeRepository;
import com.example.modiraa.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class LikeService extends LikeDislikeService {
    public LikeService(LikeRepository likeRepository, DislikeRepository dislikeRepository, MemberRepository memberRepository) {
        super(likeRepository, dislikeRepository, memberRepository);
    }

    //유저의 평가 점수 +1점 부여하고 싶을때
    public ResponseEntity<String> rateLike(UserDetailsImpl userDetails, Long userId) {
        return rate(userDetails, userId, RatingType.LIKE);
    }

    //유저의 평가를 잘못했을 경우 취소 기능
    public ResponseEntity<String> deleteLike(UserDetailsImpl userDetails, Long userId) {
        return deleteRating(userDetails, userId, RatingType.LIKE);
    }
}
