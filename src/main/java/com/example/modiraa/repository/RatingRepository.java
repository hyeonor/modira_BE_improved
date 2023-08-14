package com.example.modiraa.repository;

import com.example.modiraa.enums.RatingType;
import com.example.modiraa.model.Member;
import com.example.modiraa.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long>, RatingRepositoryCustom {
    Optional<Rating> findByGiverAndReceiver(Member giver, Member receiver);

    Optional<Rating> findByRatingTypeAndGiverAndReceiver(RatingType ratingType, Member giver, Member receiver);
}
