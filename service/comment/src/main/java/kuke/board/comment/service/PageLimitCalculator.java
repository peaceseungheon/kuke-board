package kuke.board.comment.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PageLimitCalculator {

    /**
     *
     * @param page
     * @param pageSize
     * @param movablePageCount
     * @return
     */
    public static Long calculatePageLimit(Long page, Long pageSize, Long movablePageCount) {
        return ((page - 1) / movablePageCount + 1) * pageSize * movablePageCount + 1;
    }
}
