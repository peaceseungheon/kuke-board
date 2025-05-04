package kuke.board.article.service.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor
public class ArticleCreateRequest {

    private String title;
    private String content;
    private Long writerId;
    private Long boardId;
}
