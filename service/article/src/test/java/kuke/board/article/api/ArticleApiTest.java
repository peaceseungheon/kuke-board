package kuke.board.article.api;

import kuke.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class ArticleApiTest {

    RestClient restClient = RestClient.create("http://localhost:9000");

    @Test
    void createTest(){
        ArticleResponse response = create(new ArticleCreateRequest("hi", "my content", 1L, 1L));
        System.out.println("response = " + response);
    }

    ArticleResponse create(ArticleCreateRequest request){
        return restClient.post()
                .uri("/v1/articles")
                .body(request)
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void readTest(){
        Long articleId = 177371202678521856L;
        ArticleResponse response = restClient.get()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve()
                .body(ArticleResponse.class);
        System.out.println("response = " + response);
    }

    @Test
    void updateTest(){
        ArticleResponse response = update(177371202678521856L);
        System.out.println("response = " + response);
    }

    ArticleResponse update(Long articleId){
        return restClient.put()
                .uri("/v1/articles/{articleId}", articleId)
                .body(new ArticleUpdateRequest("hi 2", "my content 22"))
                .retrieve()
                .body(ArticleResponse.class);
    }

    @Test
    void deleteTest(){
        delete(177371202678521856L);
    }

    void delete(Long articleId){
        restClient.delete()
                .uri("/v1/articles/{articleId}", articleId)
                .retrieve();
    }

    @Getter
    @AllArgsConstructor
    public static class ArticleCreateRequest {

        private String title;
        private String content;
        private Long writerId;
        private Long boardId;
    }

    @Getter
    @AllArgsConstructor
    public static class ArticleUpdateRequest {

        private String title;
        private String content;
    }
}
