package kuke.board.comment.api;

import kuke.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class CommentApiTest {

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create(){
        CommentResponse response1 = createComment(
            new CommentCreateRequest(1L, "my comment1", 1L, null));
        CommentResponse response2 = createComment(
            new CommentCreateRequest(1L, "my comment2", 1L, response1.getCommentId()));
        CommentResponse response3 = createComment(
            new CommentCreateRequest(1L, "my comment3", 1L, response1.getCommentId()));

        System.out.println("response1.getCommentId()..." + response1.getCommentId());
        System.out.println("response2.getCommentId()..." + response2.getCommentId());
        System.out.println("response3.getCommentId()..." + response3.getCommentId());

        /*response1.getCommentId()...182153949500051456
        response2.getCommentId()...182153949621686272
        response3.getCommentId()...182153949659435008*/
    }

    @Test
    void read(){
        CommentResponse response = restClient.get()
            .uri("/v1/comments/{commentId}", 182153949500051456L)
            .retrieve()
            .body(CommentResponse.class);

        System.out.println("response.getCommentId() = " + response.getCommentId());
    }

    @Test
    void delete(){
        restClient.delete()
            .uri("/v1/comments/{commentId}", 182153949659435008L)
            .retrieve();
    }

    CommentResponse createComment(CommentCreateRequest request){
        return restClient.post()
            .uri("/v1/comments")
            .body(request)
            .retrieve()
            .body(CommentResponse.class);
    }

    @Getter
    @AllArgsConstructor
    public static class CommentCreateRequest {

        private Long articleId;
        private String content;
        private Long writerId;
        private Long parentCommentId;

    }


}
