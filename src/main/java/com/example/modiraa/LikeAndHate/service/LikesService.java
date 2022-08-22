package com.example.modiraa.LikeAndHate.service;

import com.example.modiraa.LikeAndHate.model.Hates;
import com.example.modiraa.LikeAndHate.model.Likes;
import com.example.modiraa.LikeAndHate.repository.HatesRepository;
import com.example.modiraa.LikeAndHate.repository.LikesRepository;
import com.example.modiraa.loginAndRegister.auth.UserDetailsImpl;
import com.example.modiraa.loginAndRegister.model.Member;
import com.example.modiraa.loginAndRegister.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LikesService {
    private final LikesRepository likesRepository;
    private final UserRepository userRepository;

    private final HatesRepository hatesRepository;

    //유저의 평가 점수 +1점 부여하고 싶을때
    public ResponseEntity<?> userLikes(UserDetailsImpl userDetails, Long userId) {
        System.out.println("------------------------------");
        //USERID 아이디로 USER 를 찾아서 저장
        Member receiver = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저가 없습니다"));
        Member giver = userDetails.getMember();
        Optional<Likes> likesFound = likesRepository.findByGiverAndReceiver(giver, receiver);
        Optional<Hates> HatesFound = hatesRepository.findByGiverAndReceiver(giver, receiver);
        if(likesFound.isPresent()){
            return new ResponseEntity<>("중복된 좋아요는 불가능합니다.", HttpStatus.BAD_REQUEST);
        }

        if (HatesFound.isPresent()){
            return new ResponseEntity<>("한 사람의 유저에 좋아요,싫어요 둘다 평가 할 수 없습니다. ", HttpStatus.BAD_REQUEST);
        }
        System.out.println("2222222222222222222222222222222222");
        Likes likes = new Likes(giver, receiver);
        likesRepository.save(likes);
        System.out.println("3333333333333333333333");

        return new ResponseEntity<>("좋아요 성공! ", HttpStatus.valueOf(201));
    }

    //유저의 평가를 잘못 눌렀을 취소 기능
    public ResponseEntity<?> deleteLikes(UserDetailsImpl userDetails, Long userId) {
        // USERID 로 좋아요 한 게시물들을 리스트에 담아서
        Member receiver = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("유저가 없습니다"));
        Member giver = userDetails.getMember();
        Optional<Likes> likesFound = likesRepository.findByGiverAndReceiver(giver, receiver);
        if(likesFound.isEmpty()){
            return new ResponseEntity<>("좋아요 한 기록이 없습니다.", HttpStatus.BAD_REQUEST);
        }
        likesRepository.delete(likesFound.get());
        return new ResponseEntity<>("좋아요 취소 성공 .", HttpStatus.valueOf(200));
    }
}
