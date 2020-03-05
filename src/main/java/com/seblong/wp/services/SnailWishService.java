package com.seblong.wp.services;

import com.seblong.wp.entities.SnailWish;

public interface SnailWishService {

	SnailWish create(int startDate, int endDate, int startTime, int endTime, String suprisedUrl, String popupUrl,
			long popupStart, long popupEnd, String bigCouponUrl, String smallCouponUrl);

	SnailWish update(long id, int startDate, int endDate, int startTime, int endTime, String suprisedUrl, String popupUrl,
			long popupStart, long popupEnd, String bigCouponUrl, String smallCouponUrl);

}
