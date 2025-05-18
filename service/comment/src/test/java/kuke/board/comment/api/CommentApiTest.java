package kuke.board.comment.api;

import java.util.List;
import kuke.board.comment.service.response.CommentPageResponse;
import kuke.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

public class CommentApiTest {

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create() {
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
    void read() {
        CommentResponse response = restClient.get()
            .uri("/v1/comments/{commentId}", 182153949500051456L)
            .retrieve()
            .body(CommentResponse.class);

        System.out.println("response.getCommentId() = " + response.getCommentId());
    }

    @Test
    void delete() {
        restClient.delete()
            .uri("/v1/comments/{commentId}", 182153949659435008L)
            .retrieve();
    }

    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
            .uri("/v1/comments")
            .body(request)
            .retrieve()
            .body(CommentResponse.class);
    }

    @Test
    void readAll() {
        CommentPageResponse response = restClient.get()
            .uri("/v1/comments?articleId=1&page=1&pageSize=10")
            .retrieve()
            .body(CommentPageResponse.class);
        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
    }

    @Test
    void readAllInfiniteScroll() {
        List<CommentResponse> response1 = restClient.get()
            .uri("/v1/comments/infinite-scroll?articleId=1&pageSize=5")
            .retrieve()
            .body(new ParameterizedTypeReference<List<CommentResponse>>() {
            });
        System.out.println("firstPage");
        for (CommentResponse comment : response1) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        Long lastParentCommentId = response1.getLast().getParentCommentId();
        Long lastCommentId = response1.getLast().getCommentId();

        List<CommentResponse> response2 = restClient.get()
            .uri(
                "/v1/comments/infinite-scroll?articleId=1&pageSize=5&lastParentCommentId=%s&lastCommentId=%s"
                    .formatted(lastParentCommentId, lastCommentId))
            .retrieve()
            .body(new ParameterizedTypeReference<List<CommentResponse>>() {
            });
        System.out.println("secondPage");
        for (CommentResponse comment : response2) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.print("\t");
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }

        /*
        * comment.getCommentId() = 182153770675900416
	comment.getCommentId() = 182153774996033536
	comment.getCommentId() = 182153778259202048
comment.getCommentId() = 182153852624211968
	comment.getCommentId() = 182153852825538560
	comment.getCommentId() = 182153852867481600
comment.getCommentId() = 182157337885368320
	comment.getCommentId() = 182157337910534148
comment.getCommentId() = 182157337885368321
	comment.getCommentId() = 182157337910534151
	* */
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
