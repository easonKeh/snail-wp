package com.seblong.wp.services;

import com.seblong.wp.entities.SnailWish;
import com.seblong.wp.exceptions.ValidationException;

public interface SnailWishService {
	
	SnailWish get();
	
	SnailWish get(String user);

	void delete(long id);
	
	boolean isAllowBig(SnailWish snailWish, String user);
	
	SnailWish create(String startDate, String endDate, String startTime, String endTime, String suprisedUrl, String popupUrl,
			long popupStart, long popupEnd, String bigCouponUrl, String smallCouponUrl) throws ValidationException;

	SnailWish update(long id, String startDate, String endDate, String startTime, String endTime, String suprisedUrl, String popupUrl,
			long popupStart, long popupEnd, String bigCouponUrl, String smallCouponUrl) throws ValidationException;
	
	void lottery();

}
