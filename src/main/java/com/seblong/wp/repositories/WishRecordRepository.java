package com.seblong.wp.repositories;

import com.seblong.wp.entities.WishRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishRecordRepository extends JpaRepository<WishRecord, Long> {

    WishRecord findByUserAndLotteryDate(String userId, String lotteryDate);

    List<WishRecord> findByUser(String userId);

    WishRecord findByDeviceIdAndLotteryDate(String deviceId, String lotteryDate);
}
