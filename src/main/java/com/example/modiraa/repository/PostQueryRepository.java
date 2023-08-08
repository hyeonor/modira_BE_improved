package com.example.modiraa.repository;

import com.example.modiraa.dto.response.MyPostsResponse;
import com.example.modiraa.model.Post;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.modiraa.model.QPost.post;
import static com.example.modiraa.model.QPostImage.postImage;

@RequiredArgsConstructor
@Repository
public class PostQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Page<Post> findBySearchKeywordAndAddress(Long lastId, String address, String keyword, Pageable pageable) {
        QueryResults<Post> result = queryFactory.selectFrom(post)
                .where(post.id.lt(lastId).and(post.address.contains(address)))
                .where(post.menu.contains(keyword)
                        .or(post.title.contains(keyword)).or(post.contents.contains(keyword)))
                .join(post.member)
                .join(post.postImage)
                .join(post.chatRoom)
                .fetchJoin()
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    public Page<Post> findByIdLessThanAndCategory(Long lastId, String category, Pageable pageable) {
        QueryResults<Post> result = queryFactory.selectFrom(post)
                .where(post.id.lt(lastId).and(post.category.contains(category)))
                .join(post.member)
                .join(post.postImage)
                .join(post.chatRoom)
                .fetchJoin()
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    public Page<Post> findAll(Pageable pageable) {
        QueryResults<Post> result = queryFactory.selectFrom(post)
                .join(post.member)
                .join(post.postImage)
                .join(post.chatRoom)
                .fetchJoin()
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    public Page<Post> findByCategory(String category, Pageable pageable) {
        QueryResults<Post> result = queryFactory.selectFrom(post)
                .where(post.category.contains(category))
                .join(post.member)
                .join(post.postImage)
                .join(post.chatRoom)
                .fetchJoin()
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    public Page<Post> findAllByAddress(String address, Pageable pageable) {
        QueryResults<Post> result = queryFactory.selectFrom(post)
                .where(post.address.contains(address))
                .join(post.member)
                .join(post.postImage)
                .join(post.chatRoom)
                .fetchJoin()
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    public Page<Post> findByAddressAndCategory(String address, String category, Pageable pageable) {
        QueryResults<Post> result = queryFactory.selectFrom(post)
                .where(post.address.contains(address).and(post.category.contains(category)))
                .join(post.member)
                .join(post.postImage)
                .join(post.chatRoom)
                .fetchJoin()
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return new PageImpl<>(result.getResults(), pageable, result.getTotal());
    }

    public List<MyPostsResponse> findMyPostsByMemberOrderByDesc(Long memberId) {
        return queryFactory
                .select(Projections.constructor(
                        MyPostsResponse.class,
                        post.id,
                        post.title,
                        postImage.imageUrl,
                        post.menu))
                .from(post)
                .leftJoin(postImage).on(post.menu.eq(postImage.menu))
                .where(post.member.id.eq(memberId))
                .orderBy(post.id.desc())
                .fetch();
    }
}
