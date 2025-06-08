package kuke.board.comment.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import kuke.board.comment.service.response.CommentPageResponse;
import kuke.board.comment.service.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

class CommentControllerV2Test {

    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create() {
        CommentResponse response1 = create(
            new CommentCreateRequestV2(1L, "my comment1", 1L, null));
        CommentResponse response2 = create(
            new CommentCreateRequestV2(1L, "my comment2", 1L, response1.getPath()));
        CommentResponse response3 = create(
            new CommentCreateRequestV2(1L, "my comment3", 1L, response2.getPath()));

        System.out.println("response1.getCommentId() = " + response1.getCommentId());
        System.out.println("\tresponse2.getCommentId() = " + response2.getCommentId());
        System.out.println("\t\tresponse3.getCommentId() = " + response3.getCommentId());
    }

    CommentResponse create(CommentCreateRequestV2 request) {
        return restClient.post()
            .uri("/v2/comments")
            .body(request)
            .retrieve()
            .body(CommentResponse.class);
    }

    @Getter
    @AllArgsConstructor
    public class CommentCreateRequestV2 {

        private Long articleId;
        private String content;
        private Long writerId;
        private String parentPath;

    }

    @Test
    void read() {
        CommentResponse response = restClient.get()
            .uri("/v2/comments/{commentId}", 186813385261215744L)
            .retrieve()
            .body(CommentResponse.class);

        System.out.println("response = " + response);
    }

    @Test
    void delete() {
        restClient.delete()
            .uri("/v2/comments/{commentId}", 186813385261215744L)
            .retrieve();
    }

    @Test
    void readAll() {
        CommentPageResponse res = restClient.get()
            .uri("/v2/comments?articleId=1&page=50000&pageSize=10")
            .retrieve()
            .body(CommentPageResponse.class);

        for (CommentResponse comment : res.getComments()) {
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
        System.out.println("res.getCommentCount() = " + res.getCommentCount());
        /*
        * comment.getCommentId() = 189976642528776194
comment.getCommentId() = 189976642553942018
comment.getCommentId() = 189976642553942037
comment.getCommentId() = 189976642553942043
comment.getCommentId() = 189976642558136320
comment.getCommentId() = 189976642558136329
comment.getCommentId() = 189976642558136343
comment.getCommentId() = 189976642558136348
comment.getCommentId() = 189976642558136353
comment.getCommentId() = 189976642558136363
        * */
    }

    @Test
    void readAllInfiniteScroll(){
        List<CommentResponse> res = restClient.get()
            .uri("/v2/comments/infinite-scroll?articleId=1&pageSize=10&lastPath=00009")
            .retrieve()
            .body(new ParameterizedTypeReference<List<CommentResponse>>() {
            });
        for (CommentResponse comment : res) {
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
            System.out.println("comment.getPath() = " + comment.getPath());
        }
    }


}