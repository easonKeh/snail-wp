package com.seblong.wp.domains;

import java.io.Serializable;

import com.seblong.wp.entities.SnailWish;
import com.seblong.wp.entities.SnailWish.WishStatus;

import lombok.Data;

@Data
public class SnailWishDomain implements Serializable{

	private static final long serialVersionUID = -9150451637419376093L;

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

	// 倒计时，毫秒数
	private long countDown;

	private WishStatus status;

	private boolean joined;

	private boolean popup;

	private long current;
	
	private String lotteryDate;
	
	private int num;
	
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
		domain.current = snailWish.getCurrent();
		domain.joined = snailWish.isJoined();
		domain.countDown = snailWish.getCountDown();
		domain.status = snailWish.getStatus();
		domain.popup = snailWish.isPopup();
		domain.lotteryDate = snailWish.getLotteryDate();
		domain.num = snailWish.getNum();
		return domain;
	}
	
}
