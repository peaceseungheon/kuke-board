package kuke.board.article.controller;

import kuke.board.article.service.ArticleService;
import kuke.board.article.service.request.ArticleCreateRequest;
import kuke.board.article.service.request.ArticleUpdateRequest;
import kuke.board.article.service.response.ArticleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/{articleId}")
    public ArticleResponse read(@PathVariable("articleId") Long articleId){
        return articleService.read(articleId);
    }

    @PostMapping
    public ArticleResponse create(@RequestBody ArticleCreateRequest request){
        return articleService.create(request);
    }

    @PutMapping("/{articleId}")
    public ArticleResponse update(@PathVariable("articleId") Long articleId, @RequestBody ArticleUpdateRequest request){
        return articleService.update(articleId, request);
    }

    @DeleteMapping("/{articleId}")
    public void delete(@PathVariable("articleId") Long articleId){
        articleService.delete(articleId);
    }
}
