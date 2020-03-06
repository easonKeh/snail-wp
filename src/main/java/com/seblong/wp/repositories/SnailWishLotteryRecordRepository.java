package com.seblong.wp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seblong.wp.entities.SnailWish.AwardType;
import com.seblong.wp.entities.SnailWishLotteryRecord;

@Repository
public interface SnailWishLotteryRecordRepository extends JpaRepository<SnailWishLotteryRecord, Long>{

	Page<SnailWishLotteryRecord> findByLotteryDateAndType(String lotteryDate, AwardType type, Pageable pageable);
	
	SnailWishLotteryRecord findByUserAndLotteryDate(String user, String lotteryDate);
}
