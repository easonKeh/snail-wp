package com.seblong.wp.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.seblong.wp.entities.SnailWish.AwardType;
import com.seblong.wp.entities.SnailWishLotteryRecord;
import com.seblong.wp.entities.mongo.User;
import com.seblong.wp.repositories.SnailWishLotteryRecordRepository;
import com.seblong.wp.services.SnailWishLotteryRecordService;
import com.seblong.wp.services.UserService;

@Service
public class SnailWishLotteryRecordServiceImpl implements SnailWishLotteryRecordService {

	@Autowired
	private SnailWishLotteryRecordRepository snailWishLotteryRecordRepo;
	
	@Autowired
	private UserService userService;
	
	@Override
	public SnailWishLotteryRecord create(String userId, String lotteryDate, AwardType type) {
		User user = userService.getBriefUser(userId);
		SnailWishLotteryRecord record = null;
		if( user != null ) {
			record = new SnailWishLotteryRecord(type, lotteryDate, userId, user.getName(), user.getuId(), user.getAvatar());
		}else {
			record = new SnailWishLotteryRecord(type, lotteryDate, userId, "", 0, "");
		}
		try {
			record = snailWishLotteryRecordRepo.save(record);
		}catch (DataIntegrityViolationException e) {
			record = get(userId, lotteryDate);
		}
		return record;
	}

	@Override
	public SnailWishLotteryRecord get(String userId, String lotteryDate) {
		return snailWishLotteryRecordRepo.findByUserAndLotteryDate(userId, lotteryDate);
	}

	@Override
	public Page<SnailWishLotteryRecord> list(String lotteryDate, AwardType type, int page) {
		if( page <= 0 ) {
			page = 1;
		}
		Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Direction.ASC, "id"));
		
		return snailWishLotteryRecordRepo.findByLotteryDateAndType(lotteryDate, type, pageable);
	}

}
