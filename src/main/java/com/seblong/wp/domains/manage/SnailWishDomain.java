package com.seblong.wp.domains.manage;

import java.io.Serializable;

import com.seblong.wp.entities.SnailWish;

import lombok.Data;

@Data
public class SnailWishDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8447002868084841494L;

	private String unique;

	// 开始日期
	private String startDate;

	// 结束日期
	private String endDate;

	// 每天的开始时间
	private String startTime;

	// 每天的结束时间
	private String endTime;

	// 惊喜链接
	private String suprisedUrl;

	// 弹窗链接
	private String popupUrl;

	// 弹窗开始时间戳
	private long popupStart;

	// 弹窗结束时间戳
	private long popupEnd;

	// 大额优惠券地址
	private String bigCouponUrl;

	// 中额优惠卷地址
	private String smallCouponUrl;

	
	private SnailWishDomain () {
	}

	public static SnailWishDomain fromEntity(SnailWish snailWish) {
		SnailWishDomain domain = new SnailWishDomain();
		domain.unique = String.valueOf(snailWish.getId());
		domain.startDate = snailWish.getStartDate();
		domain.endDate = snailWish.getEndDate();
		domain.startTime = snailWish.getStartTime();
		domain.endTime = snailWish.getEndTime();
		domain.suprisedUrl = snailWish.getSuprisedUrl();
		domain.popupUrl = snailWish.getPopupUrl();
		domain.popupStart = snailWish.getPopupStart();
		domain.popupEnd = snailWish.getPopupEnd();
		domain.bigCouponUrl = snailWish.getBigCouponUrl();
		domain.smallCouponUrl = snailWish.getSmallCouponUrl();
		return domain;
	}
	
}
