package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
class PointServiceTest {

    private PointService pointService;

    @BeforeEach
    void setUp() {
        UserPointTable userPointTable = new UserPointTable();
        PointHistoryTable pointHistoryTable = new PointHistoryTable();

        pointService = new PointService(userPointTable, pointHistoryTable);
    }

    @Test
    void getUserPoint() {
        // 유저의 포인트를 조회하는 메서드

        // given
        long userId = 1L;

        // when
        long userPoint = pointService.getUserPoint(userId);

        // then
        assertThat(userPoint).isEqualTo(0);
    }

    @Test
    void chargePoint() {
        // 특정 유저의 포인트를 충전하는 메서드

        // given
        long userId = 1L;

        // when
        UserPoint userPoint = pointService.chargePoint(userId, 1000);

        // then
        assertThat(userPoint.point()).isEqualTo(1000);
    }

    @Test
    void usePoint() {
        // 포인트 사용 메서드

        // given
//        UserPoint userPoint = new UserPoint(1L, 1000, System.currentTimeMillis());
        UserPoint userPoint = pointService.chargePoint(1L, 1000);

        // when
        pointService.usePoint(userPoint.id(), 2000);

        // then
        assertThat(pointService.getUserPoint(userPoint.id())).isEqualTo(700);
    }

    @Test
    void getUserHistory() {
        // 유저의 포인트 내역 조회

        // given
        pointService.chargePoint(1L, 1000);
        pointService.chargePoint(1L, 500);
        pointService.usePoint(1L, 300);
        pointService.chargePoint(1L, 500);
        pointService.usePoint(1L, 500);

        // when
        List<PointHistory> history = pointService.getUserHistory(1L);

        // then
        // 포인트 내역 조회 내역 수가 같은지?
        assertThat(history).hasSize(5);

        // userId 1L 만 포함되었는지?
        assertThat(history).allMatch(h -> h.userId() == 1L);

        // 내역의 type이 모두 같은지?
        assertThat(history).extracting("type").containsExactly(
                TransactionType.CHARGE,
                TransactionType.CHARGE,
                TransactionType.USE,
                TransactionType.CHARGE,
                TransactionType.USE
        );
    }
}