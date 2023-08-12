package com.example.modiraa.service;

import com.example.modiraa.auth.UserDetailsImpl;
import com.example.modiraa.enums.RatingType;
import com.example.modiraa.repository.DislikeRepository;
import com.example.modiraa.repository.LikeRepository;
import com.example.modiraa.repository.MemberRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class DislikeService extends LikeDislikeService {
    public DislikeService(LikeRepository likeRepository, DislikeRepository dislikeRepository, MemberRepository memberRepository) {
        super(likeRepository, dislikeRepository, memberRepository);
    }

    //유저의 평가 점수 -1점 부여하고 싶을때
    public ResponseEntity<String> rateDislike(UserDetailsImpl userDetails, Long userId) {
        return rate(userDetails, userId, RatingType.DISLIKE);
    }


    //유저의 평가를 잘못 눌렀을 취소 기능
    public ResponseEntity<String> deleteDislike(UserDetailsImpl userDetails, Long userId) {
        return deleteRating(userDetails, userId, RatingType.DISLIKE);
    }
}
