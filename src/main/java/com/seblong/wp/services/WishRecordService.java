package com.seblong.wp.services;

import com.seblong.wp.entities.WishRecord;

import java.util.List;

public interface WishRecordService {
    WishRecord findByUserAndDate(String userId, String lotteryDate);

    WishRecord findByDeviceIdAndDate(String deviceId, String lotteryDate);

    WishRecord wishing(String userId, String deviceId, String lotteryDate);

    List<WishRecord> listByUser(String userId);
}
