package com.seblong.wp.services.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.seblong.wp.entities.SnailWish;
import com.seblong.wp.entities.SnailWish.WishStatus;
import com.seblong.wp.exceptions.ValidationException;
import com.seblong.wp.repositories.SnailWishRepository;
import com.seblong.wp.repositories.WishRecordRepository;
import com.seblong.wp.services.SnailWishService;
import com.seblong.wp.utils.RedisLock;

@Service
public class SnailWishServiceImpl implements SnailWishService {

	@Autowired
	private SnailWishRepository snailWishRepo;
	
	@Autowired
	private WishRecordRepository wishRecordRepo;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	private final static String key = "SNAIL::WISH";

	@Override
	public SnailWish create(String startDate, String endDate, String startTime, String endTime, String suprisedUrl,
			String popupUrl, long popupStart, long popupEnd, String bigCouponUrl, String smallCouponUrl) throws ValidationException{
		if( get() != null ) {
			throw new ValidationException(400, "snailwish-exist");
		}
		
		SnailWish snailWish = new SnailWish(startDate, endDate, startTime, endTime, suprisedUrl, popupUrl, popupStart, popupEnd, bigCouponUrl, smallCouponUrl);
		snailWish.calculateStart();
		snailWish.calculateEnd();
		return snailWishRepo.save(snailWish);
	}

	@Override
	public SnailWish update(long id, String startDate, String endDate, String startTime, String endTime,
			String suprisedUrl, String popupUrl, long popupStart, long popupEnd, String bigCouponUrl,
			String smallCouponUrl) throws ValidationException{
		Optional<SnailWish> optional = snailWishRepo.findById(id);
		if( !optional.isPresent() ) {
			throw new ValidationException(404, "snailwish-not-exist");
		}
		SnailWish snailWish = optional.get();
		snailWish.setStartDate(startDate);
		snailWish.setEndDate(endDate);
		snailWish.setStartTime(startTime);
		snailWish.setEndTime(endTime);
		snailWish.setSuprisedUrl(suprisedUrl);
		snailWish.setPopupUrl(popupUrl);
		snailWish.setPopupStart(popupStart);
		snailWish.setPopupEnd(popupEnd);
		snailWish.setBigCouponUrl(bigCouponUrl);
		snailWish.setSmallCouponUrl(smallCouponUrl);
		snailWish.calculateStart();
		snailWish.calculateEnd();
		removeSnailWish();
		return snailWishRepo.save(snailWish);
	}

	@Override
	public SnailWish get() {
		SnailWish snailWish = loadSnailWish();
		if( snailWish == null ) {
			List<SnailWish> snailWishes = snailWishRepo.findAll();
			if( !CollectionUtils.isEmpty(snailWishes) ) {
				snailWish = snailWishes.get(0);
				putSnailWish(snailWish);
				snailWish.calculateStatus();
			}
		}else {
			snailWish.calculateStatus();
		}
		return snailWish;
	}

	@Override
	public SnailWish get(String user) {
		SnailWish snailWish = loadSnailWish();
		if( snailWish == null ) {
			List<SnailWish> snailWishes = snailWishRepo.findAll();
			if( !CollectionUtils.isEmpty(snailWishes) ) {
				snailWish = snailWishes.get(0);
				putSnailWish(snailWish);
			}
		}
		if( snailWish != null ) {
			snailWish.calculate();
			//判断用户是否已经参加
			if (wishRecordRepo.countByUserAndLotteryDate(user, snailWish.getLotteryDate()) > 0) {
				//若已经参加则调用snailWish.joined();
				snailWish.joined();
			}
		}
		return snailWish;
	}
	
	@Override
	public void delete(long id) {
		List<SnailWish> snailWishes = snailWishRepo.findAll();
		if( !CollectionUtils.isEmpty(snailWishes) ) {
			SnailWish snailWish = snailWishes.get(0);
			snailWishRepo.delete(snailWish);
			removeSnailWish();
			clearBigUser(snailWish);
		}
		
		
	}
	
	@Override
	public boolean isAllowBig(SnailWish snailWish, String user) {
		return !isBigUser(snailWish, user);
	}
	
	@Scheduled(cron = "0 55 11 * * ?")
	@Override
	public void lottery() {
		String lockKey = "WISH::LOTTERY:LOCK";
		
		RedisLock redisLock= new RedisLock(redisTemplate, lockKey);
		if(redisLock.tryLock()) {
			SnailWish snailWish = get();
			if( snailWish != null && snailWish.getStatus().equals(WishStatus.WAIT_LOTTERY) ) {
				LocalDate nowLocalDate = LocalDate.now();
				String nowLocalDateStr = nowLocalDate.format(DateTimeFormatter.BASIC_ISO_DATE);
				if( nowLocalDateStr.equals(snailWish.getLotteryDate()) ) {
					//80,200
					long total = wishRecordRepo.countByLotteryDate(snailWish.getLotteryDate());
					if( total <= 500 ) {
						//一次性全取出开奖
						
					}else {
						//分页取出开奖
						//100/total * 80
						//100/total * 200
					}
				}
			}
			
			redisLock.unlock();
		}
		
	}
	
	private void putSnailWish(SnailWish snailWish) {
		redisTemplate.boundValueOps(key).set(snailWish);
	}
	
	private SnailWish loadSnailWish() {
		Object result = redisTemplate.boundValueOps(key).get();
		if( result != null ) {
			return (SnailWish)result;
		}
		return null;
	}
	
	private void removeSnailWish() {
		redisTemplate.delete(key);
	}

	private void putBigUser(SnailWish snailWish, List<String> users) {
		String key = "SNAIL::WISH::BIG::" + snailWish.getId();
		redisTemplate.boundSetOps(key).add(users);
	}
	
	private boolean isBigUser(SnailWish snailWish, String user) {
		String key = "SNAIL::WISH::BIG::" + snailWish.getId();
		return redisTemplate.boundSetOps(key).isMember(user);
	}
	
	private void clearBigUser(SnailWish snailWish) {
		String key = "SNAIL::WISH::BIG::" + snailWish.getId();
		redisTemplate.delete(key);
	}
}
