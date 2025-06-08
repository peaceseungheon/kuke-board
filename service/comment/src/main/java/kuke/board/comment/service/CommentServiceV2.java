package kuke.board.comment.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import kuke.board.comment.entity.CommentPath;
import kuke.board.comment.entity.CommentV2;
import kuke.board.comment.repository.CommentRepositoryV2;
import kuke.board.comment.service.request.CommentCreateRequestV2;
import kuke.board.comment.service.response.CommentPageResponse;
import kuke.board.comment.service.response.CommentResponse;
import kuke.board.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentServiceV2 {

    private final Snowflake snowflake = new Snowflake();
    private final CommentRepositoryV2 commentRepositoryV2;


    @Transactional
    public CommentResponse create(CommentCreateRequestV2 request) {
        CommentV2 parent = findParent(request);
        CommentPath parentCommentPath =
            parent == null ? CommentPath.create("") : parent.getCommentPath();
        CommentV2 comment = commentRepositoryV2.save(
            CommentV2.create(
                snowflake.nextId(),
                request.getContent(),
                request.getArticleId(),
                request.getWriterId(),
                parentCommentPath.createChildCommentPath(
                    commentRepositoryV2.findDescendantsTopPath(
                        request.getArticleId(),
                        parentCommentPath.getPath()
                    ).orElse(null)
                )
            )
        );
        return CommentResponse.from(comment);
    }

    private CommentV2 findParent(CommentCreateRequestV2 request) {
        String parentPath = request.getParentPath();
        if (parentPath == null) {
            return null;
        }
        return commentRepositoryV2.findByPath(parentPath)
            .filter(Predicate.not(CommentV2::getDeleted))
            .orElseThrow();
    }

    public CommentResponse read(Long commentId) {
        return CommentResponse.from(
            commentRepositoryV2.findById(commentId).orElseThrow()
        );
    }

    public void delete(Long commentId) {
        commentRepositoryV2.findById(commentId)
            .filter(Predicate.not(CommentV2::getDeleted))
            .ifPresent(comment -> {
                if (hasChild(comment)) {
                    comment.delete();
                } else {
                    delete(comment);
                }
            });

    }

    private void delete(CommentV2 comment) {
        commentRepositoryV2.delete(comment);
        if (!comment.isRoot()) {
            commentRepositoryV2.findByPath(comment.getCommentPath().getParentPath())
                .filter(CommentV2::getDeleted) // 상위 댓글이 삭제 상태이고
                .filter(Predicate.not(this::hasChild)) // 하위 댓글이 없다면
                .ifPresent(this::delete); // 삭제한다.
        }
    }

    private boolean hasChild(CommentV2 comment) {
        return commentRepositoryV2.findDescendantsTopPath(
            comment.getArticleId(),
            comment.getCommentPath().getPath()
        ).isPresent();
    }

    public CommentPageResponse readAll(Long articleId, Long page, Long pageSize) {
        return CommentPageResponse.of(
            commentRepositoryV2.findAll(articleId, (page - 1) * pageSize, pageSize)
                .stream()
                .map(CommentResponse::from)
                .toList(),
            commentRepositoryV2.count(articleId, PageLimitCalculator.calculatePageLimit(page, pageSize, 10L))
        );
    }

    public List<CommentResponse> readAllInfiniteScroll(Long articleId, String lastPath, Long pageSize){
        if (lastPath == null){
            return commentRepositoryV2.findAllInfiniteScroll(articleId, pageSize)
                .stream()
                .map(CommentResponse::from)
                .toList();
        }else{
            return commentRepositoryV2.findAllInfiniteScroll(articleId, lastPath, pageSize)
                .stream()
                .map(CommentResponse::from)
                .toList();
        }
    }
}
