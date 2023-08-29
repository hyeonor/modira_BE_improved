package com.example.modiraa.repository;

import com.example.modiraa.dto.response.MyPostsResponse;
import com.example.modiraa.model.Post;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.modiraa.model.QChatRoom.chatRoom;
import static com.example.modiraa.model.QPost.post;
import static com.example.modiraa.model.QPostImage.postImage;

@RequiredArgsConstructor
@Repository
public class PostRepositoryImpl implements PostRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> findBySearchKeywordAndAddress(Long lastId, String address, String keyword, Pageable pageable) {
        List<Post> result = queryFactory.selectFrom(post)
                .join(post.postImage, postImage).fetchJoin()
                .join(post.chatRoom, chatRoom).fetchJoin()
                .where(post.id.lt(lastId)
                        .and(post.address.contains(address))
                        .and(post.postImage.menu.contains(keyword)
                                .or(post.title.contains(keyword))
                                .or(post.contents.contains(keyword))
                        )
                )
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .join(post.postImage, postImage)
                .where(post.address.contains(address)
                        .and(post.postImage.menu.contains(keyword)
                                .or(post.title.contains(keyword))
                                .or(post.contents.contains(keyword))
                        )
                );

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Post> findByIdLessThanAndCategory(Long lastId, String category, Pageable pageable) {
        List<Post> result = queryFactory.selectFrom(post)
                .join(post.postImage).fetchJoin()
                .join(post.chatRoom).fetchJoin()
                .where(post.id.lt(lastId).and(post.category.stringValue().contains(category)))
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(post.category.stringValue().contains(category));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Post> findAllPosts(Pageable pageable) {
        List<Post> result = queryFactory.selectFrom(post)
                .join(post.postImage).fetchJoin()
                .join(post.chatRoom).fetchJoin()
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post);

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Post> findByCategory(String category, Pageable pageable) {
        List<Post> result = queryFactory.selectFrom(post)
                .join(post.postImage).fetchJoin()
                .join(post.chatRoom).fetchJoin()
                .where(post.category.stringValue().contains(category))
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(post.category.stringValue().contains(category));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Post> findAllByAddress(String address, Pageable pageable) {
        List<Post> result = queryFactory.selectFrom(post)
                .join(post.postImage).fetchJoin()
                .join(post.chatRoom).fetchJoin()
                .where(post.address.contains(address))
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(post.address.contains(address));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public Page<Post> findByAddressAndCategory(String address, String category, Pageable pageable) {
        List<Post> result = queryFactory.selectFrom(post)
                .join(post.postImage).fetchJoin()
                .join(post.chatRoom).fetchJoin()
                .where(post.address.contains(address)
                        .and(post.category.stringValue().contains(category)))
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(post.count())
                .from(post)
                .where(post.address.contains(address)
                        .and(post.category.stringValue().contains(category)));

        return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
    }

    @Override
    public List<MyPostsResponse> findMyPostsByMemberOrderByDesc(Long memberId) {
        return queryFactory
                .select(Projections.constructor(
                        MyPostsResponse.class,
                        post.id,
                        post.title,
                        postImage.imageUrl,
                        post.postImage.menu))
                .from(post)
                .leftJoin(post.postImage, postImage)
                .where(post.owner.id.eq(memberId))
                .orderBy(post.id.desc())
                .fetch();
    }
}
