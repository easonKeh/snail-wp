package com.seblong.wp.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Table(name = "t_snail_wish")
@Data
public class SnailWish {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;
	
	@Column(name = "STARTDATE")
	//开始日期
	private int startDate;
	
	@Column(name = "ENDDATE")
	//结束日期
	private int endDate;
	
	@Column(name = "STARTTIME")
	//每天的开始时间
	private int startTime;
	
	@Column(name = "ENDTIME")
	//每天的结束时间
	private int endTime;
	
	@Column(name = "SUPRISEDURL", nullable = true, columnDefinition = "varchar(255)")
	//惊喜链接
	private String suprisedUrl;
	
	@Column(name = "POPUPURL", nullable = true, columnDefinition = "varchar(255)")
	//弹窗链接
	private String popupUrl;
	
	@Column(name = "POPUPSTART")
	//弹窗开始时间戳
	private long popupStart;
	
	@Column(name = "POPUPEND")
	//弹窗结束时间戳
	private long popupEnd;
	
	@Column(name = "BIGCOUPONURL", nullable = true, columnDefinition = "varchar(255)")
	//大额优惠券地址
	private String bigCouponUrl;
	
	@Column(name = "SMALLCOUPONURL", nullable = true, columnDefinition = "varchar(255)")
	//中额优惠卷地址
	private String smallCouponUrl;
	
	//倒计时，毫秒数
	@Transient
	private long countDown;
	
	@Transient
	private WishStatus status;
	
	@Transient
	private boolean joined;
	
	@Transient
	private boolean popup;
	
	public SnailWish() {
	}

	public static enum AwardType {
		GOODS, COUPON_BIG, COUPON_SMALL
	}
	
	public static enum WishStatus {
		START, NOT_START
	}
	
	public void initialize() {
		
	}
	
}
