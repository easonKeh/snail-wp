package com.seblong.wp.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.seblong.wp.entities.SnailWish;
import com.seblong.wp.repositories.SnailWishRepository;
import com.seblong.wp.services.SnailWishService;

@Service
public class SnailWishServiceImpl implements SnailWishService {

	@Autowired
	private SnailWishRepository snailWishRepo;
	
	@Override
	public SnailWish create(int startDate, int endDate, int startTime, int endTime, String suprisedUrl, String popupUrl,
			long popupStart, long popupEnd, String bigCouponUrl, String smallCouponUrl) {
		return null;
	}

	@Override
	public SnailWish update(long id, int startDate, int endDate, int startTime, int endTime, String suprisedUrl,
			String popupUrl, long popupStart, long popupEnd, String bigCouponUrl, String smallCouponUrl) {
		return null;
	}

}
