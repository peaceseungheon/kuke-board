package kuke.board.article.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageLimitCalculatorTest {

    @Test
    void pageLimitCalculatorTest(){
        pageLimitCalculatorTest(1L, 30L, 10L, 301L);
        pageLimitCalculatorTest(7L, 30L, 10L, 301L);
        pageLimitCalculatorTest(10L, 30L, 10L, 301L);
        pageLimitCalculatorTest(11L, 30L, 10L, 601L);
        pageLimitCalculatorTest(17L, 30L, 10L, 601L);

    }


    void pageLimitCalculatorTest(Long page, Long pageSize, Long movablePageCount, Long expected){
        Long count = PageLimitCalculator.calculatePageLimit(page, pageSize, movablePageCount);
        assertEquals(expected, count);
    }

}