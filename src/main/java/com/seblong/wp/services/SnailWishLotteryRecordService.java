package com.seblong.wp.services;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;

import com.seblong.wp.entities.SnailWishLotteryRecord;
import com.seblong.wp.entities.WishRecord;
import com.seblong.wp.entities.SnailWish.AwardType;

public interface SnailWishLotteryRecordService {

	List<SnailWishLotteryRecord> create( Collection<WishRecord> wishRecords );
	
	SnailWishLotteryRecord create( String userId, String lotteryDate, AwardType type );
	
	SnailWishLotteryRecord get( String userId, String lotteryDate );
	
	Page<SnailWishLotteryRecord> list( String lotteryDate, AwardType type, int page );
}
