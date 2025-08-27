package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    /**
     * 유저의 포인트를 조회하는 메서드
     * @param id
     * @return
     */
    public long getUserPoint(long id) {

        UserPoint userPoint = userPointTable.selectById(id);

        return userPoint.point();
    }

    /**
     * 특정 유저의 포인트를 충전하는 메서드
     * @param id
     * @param amount
     * @return
     */
    public UserPoint chargePoint(long id, long amount) {

        // 유저 조회
        // 유저의 현재 포인트 조회
        // 현재 포인트 + 충전 포인트 합산

        UserPoint userPoint = userPointTable.selectById(id);
        long nowPoint = userPoint.point();
        long totalPoint = nowPoint + amount;

        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

        return userPointTable.insertOrUpdate(id, totalPoint);
    }

    /**
     *  포인트 사용 메서드
     * @param id
     * @param amount
     */
    public void usePoint(long id, long amount) {

        // 유저 조회
        // 포인트 사용(차감)
        // 객체 저장

        UserPoint userPoint = userPointTable.selectById(id);
        long nowPoint = 0;
        try {
            nowPoint = userPoint.point();
            nowPoint -= amount;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());

        userPointTable.insertOrUpdate(id, nowPoint);
    }

    /**
     * 유저의 포인트 내역 조회
     * @param id
     * @return
     */
    public List<PointHistory> getUserHistory(long id) {

        return pointHistoryTable.selectAllByUserId(id);
    }
}