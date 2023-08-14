package com.example.modiraa.repository;

public interface RatingRepositoryCustom {
    Long countLikesForReceiver(Long receiver);

    Long countDislikesForReceiver(Long receiver);

    Long calculateScore(Long receiver);
}
