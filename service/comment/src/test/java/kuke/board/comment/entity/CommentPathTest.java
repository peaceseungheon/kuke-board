package kuke.board.comment.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CommentPathTest {

    @Test
    void createChildCommentChild(){
        // 00000 <- 생성

        createChildCommentTest(CommentPath.create(""), null, "00000");

        //00000
        //  00000 <- 생성
        createChildCommentTest(CommentPath.create("00000"), null, "0000000000");

        //00000
        //00001 <- 생성
        createChildCommentTest(CommentPath.create(""), "00000", "00001");

        //0000z
        //  abcdz
        //      zzzzz
        //          zzzzz
        //  abce0
        createChildCommentTest(CommentPath.create("0000z"), "0000zabcdzzzzzzzzzzz", "0000zabce0");
    }

    void createChildCommentTest(CommentPath commentPath, String descendantsTopPath, String expectedChildPath){
        CommentPath childCommnetPath = commentPath.createChildCommentPath(descendantsTopPath);
        assertThat(childCommnetPath.getPath()).isEqualTo(expectedChildPath);
    }

    @Test
    void createChildCommentPathIfMaxDepthTest(){
        assertThatThrownBy(()-> CommentPath.create("zzzzz".repeat(5)).createChildCommentPath(null))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createChildCommentPathIfChunkOverflowTest(){
        //given
        CommentPath commentPath = CommentPath.create("");

        //when, then
        assertThatThrownBy(()-> commentPath.createChildCommentPath("zzzzz")).isInstanceOf(IllegalStateException.class);
    }
}