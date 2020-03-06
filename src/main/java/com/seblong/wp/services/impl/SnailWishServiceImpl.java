package com.seblong.wp.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.seblong.wp.entities.SnailWish;
import com.seblong.wp.exceptions.ValidationException;
import com.seblong.wp.repositories.SnailWishRepository;
import com.seblong.wp.services.SnailWishService;

@Service
public class SnailWishServiceImpl implements SnailWishService {

	@Autowired
	private SnailWishRepository snailWishRepo;
	
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
		remove();
		return snailWishRepo.save(snailWish);
	}

	@Override
	public SnailWish get() {
		SnailWish snailWish = load();
		if( snailWish == null ) {
			List<SnailWish> snailWishes = snailWishRepo.findAll();
			if( !CollectionUtils.isEmpty(snailWishes) ) {
				snailWish = snailWishes.get(0);
				put(snailWish);
				snailWish.calculateStatus();
			}
		}else {
			snailWish.calculateStatus();
		}
		return snailWish;
	}

	@Override
	public SnailWish get(String user) {
		SnailWish snailWish = load();
		if( snailWish == null ) {
			List<SnailWish> snailWishes = snailWishRepo.findAll();
			if( !CollectionUtils.isEmpty(snailWishes) ) {
				snailWish = snailWishes.get(0);
				put(snailWish);
			}
		}
		if( snailWish != null ) {
			snailWish.calculate();
			//判断用户是否已经参加
			//若已经参加则调用snailWish.joined();
		}
		return snailWish;
	}
	
	private void put(SnailWish snailWish) {
		redisTemplate.boundValueOps(key).set(snailWish);
	}
	
	private SnailWish load() {
		Object result = redisTemplate.boundValueOps(key).get();
		if( result != null ) {
			return (SnailWish)result;
		}
		return null;
	}
	
	private void remove() {
		redisTemplate.delete(key);
	}
}
