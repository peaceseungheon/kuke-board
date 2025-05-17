package kuke.board.article.api;

import java.util.List;
import kuke.board.article.service.response.ArticlePageResponse;
import kuke.board.article.service.response.ArticleResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
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

    @Test
    void readAllTest(){
        ArticlePageResponse response = restClient.get()
                .uri("/v1/articles?boardId=1&pageSize=30&page=1")
                .retrieve()
                .body(ArticlePageResponse.class);
        System.out.println("response.getArticleCount() = " + response.getArticleCount());
        for (ArticleResponse article : response.getArticles()) {
            System.out.println("articleId = " + article.getArticleId());
        }
    }

    @Test
    void readAllInfiniteScroll(){
        List<ArticleResponse> article1 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5")
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {});

        for (ArticleResponse response : article1) {
            System.out.println("articleResponse.getArticleId() = " + response.getArticleId());
        }

        long lastArticleId = article1.getLast().getArticleId();

        List<ArticleResponse> article2 = restClient.get()
                .uri("/v1/articles/infinite-scroll?boardId=1&pageSize=5&lastArticleId={lastArticleId}", lastArticleId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<ArticleResponse>>() {
                });
        for (ArticleResponse response : article2) {
            System.out.println("articleResponse.getArticleId() = " + response.getArticleId());
        }
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
