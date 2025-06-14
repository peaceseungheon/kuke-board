package kuke.board.like.service;

import java.util.Optional;
import kuke.board.common.snowflake.Snowflake;
import kuke.board.like.entity.ArticleLike;
import kuke.board.like.repository.ArticleLikeRepository;
import kuke.board.like.service.response.ArticleLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {

    Snowflake snowflake = new Snowflake();

    private final ArticleLikeRepository articleLikeRepository;


    public ArticleLikeResponse read(Long articleId, Long userId){
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            .map(ArticleLikeResponse::from)
            .orElseThrow();
    }

    @Transactional
    public void like(Long articleId, Long userId){
        ArticleLike articleLike = ArticleLike.create(
            snowflake.nextId(),
            articleId,
            userId
        );
        articleLikeRepository.save(articleLike);
    }

    @Transactional
    public void unlike(Long articleId, Long userId){
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
            .ifPresent(articleLikeRepository::delete);
    }

}
