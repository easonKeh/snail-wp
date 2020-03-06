package com.seblong.wp.services.impl;

import com.seblong.wp.entities.WishRecord;
import com.seblong.wp.enums.EntityStatus;
import com.seblong.wp.repositories.WishRecordRepository;
import com.seblong.wp.services.WishRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishRecordServiceImpl implements WishRecordService {

    @Autowired
    private WishRecordRepository wishRecordRepo;


    @Override
    public WishRecord findByUserAndDate(String userId, String lotteryDate) {
        return wishRecordRepo.findByUserAndLotteryDate(userId, lotteryDate);
    }

    @Override
    public WishRecord findByDeviceIdAndDate(String deviceId, String lotteryDate) {
        return wishRecordRepo.findByDeviceIdAndLotteryDate(deviceId, lotteryDate);
    }

    @Override
    public WishRecord wishing(String userId, String deviceId, String lotteryDate) {
        WishRecord record = new WishRecord();
        record.setCreated(System.currentTimeMillis());
        record.setDeviceId(deviceId);
        record.setLotteryDate(lotteryDate);
        record.setStatus(EntityStatus.ACTIVED.toString());
        record.setUpdated(System.currentTimeMillis());
        record.setUser(userId);
        record = wishRecordRepo.save(record);
        return record;
    }

    @Override
    public List<WishRecord> listByUser(String userId) {
        return wishRecordRepo.findByUser(userId);
    }
}
