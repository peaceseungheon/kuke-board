package kuke.board.like.api;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kuke.board.like.service.response.ArticleLikeResponse;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

public class LikeApiTest {

    RestClient restClient = RestClient.create("http://localhost:9002");

    @Test
    void likeAndUnlikeTest(){
        Long articleId = 9999L;

        like(articleId, 1L, "pessimistic-lock-1");
        like(articleId, 2L, "pessimistic-lock-1");
        like(articleId, 3L, "pessimistic-lock-1");

        ArticleLikeResponse res1 = read(articleId, 1L);
        ArticleLikeResponse res2 = read(articleId, 2L);
        ArticleLikeResponse res3 = read(articleId, 3L);

        System.out.println("res1 = " + res1);
        System.out.println("res2 = " + res2);
        System.out.println("res3 = " + res3);

        unlike(articleId, 1L, "pessimistic-lock-1");
        unlike(articleId, 2L, "pessimistic-lock-1");
        unlike(articleId, 3L, "pessimistic-lock-1");
    }

    void like(Long articleId, Long userId, String lockType){
        restClient.post()
            .uri("/v1/article-likes/articles/{articleId}/users/{userId}/%s".formatted(lockType), articleId, userId)
            .retrieve();
    }

    void unlike(Long articleId, Long userId, String lockType){
        restClient.delete()
            .uri("/v1/article-likes/articles/{articleId}/users/{userId}/%s".formatted(lockType), articleId, userId)
            .retrieve();
    }

    ArticleLikeResponse read(Long articleId, Long userId){
        return restClient.get()
            .uri("/v1/article-likes/articles/{articleId}/users/{userId}", articleId, userId)
            .retrieve()
            .body(ArticleLikeResponse.class);
    }

    @Test
    void likePerformanceTest() throws InterruptedException{
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        likePerformanceTest(executorService, 1111L, "pessimistic-lock-1");
        likePerformanceTest(executorService, 2222L, "pessimistic-lock-2");
        likePerformanceTest(executorService, 3333L, "optimistic-lock");
    }

    void likePerformanceTest(ExecutorService executorService, Long articleId, String lockType) throws InterruptedException{
        CountDownLatch countDownLatch = new CountDownLatch(3000);
        System.out.println(lockType + " start");

        like(articleId, 1L, lockType);

        long start = System.nanoTime();
        for(int i = 0; i < 3000; i++){
            long userId = i + 2;
            executorService.submit(()-> {
                like(articleId, userId, lockType);
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();

        long end = System.nanoTime();

        System.out.println("lockType = " + lockType + ", time = " + (end - start) / 1000000 + "ms");
        System.out.println("end = " + end);

        Long count = restClient.get()
            .uri("/v1/article-likes/articles/{articleId}/count", articleId)
            .retrieve()
            .body(Long.class);

        System.out.println("count = " + count);
    }


}
