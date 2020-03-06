package com.seblong.wp.services;

import org.springframework.data.domain.Page;

import com.seblong.wp.entities.SnailWishLotteryRecord;
import com.seblong.wp.entities.SnailWish.AwardType;

public interface SnailWishLotteryRecordService {

	SnailWishLotteryRecord create( String userId, String lotteryDate, AwardType type );
	
	SnailWishLotteryRecord get( String userId, String lotteryDate );
	
	Page<SnailWishLotteryRecord> list( String lotteryDate, AwardType type, int page );
}
