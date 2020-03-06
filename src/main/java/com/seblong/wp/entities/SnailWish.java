package com.seblong.wp.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

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
public class SnailWish implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2238538861032066265L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	@Column(name = "STARTDATE", nullable = false, columnDefinition = "varchar(12)")
	// 开始日期
	private String startDate;

	@Column(name = "ENDDATE", nullable = false, columnDefinition = "varchar(12)")
	// 结束日期
	private String endDate;

	@Column(name = "STARTTIME", nullable = false, columnDefinition = "varchar(12)")
	// 每天的开始时间
	private String startTime;

	@Column(name = "ENDTIME", nullable = false, columnDefinition = "varchar(12)")
	// 每天的结束时间
	private String endTime;

	@Column(name = "SUPRISEDURL", nullable = true, columnDefinition = "varchar(255)")
	// 惊喜链接
	private String suprisedUrl;

	@Column(name = "POPUPURL", nullable = true, columnDefinition = "varchar(255)")
	// 弹窗链接
	private String popupUrl;

	@Column(name = "POPUPSTART")
	// 弹窗开始时间戳
	private long popupStart;

	@Column(name = "POPUPEND")
	// 弹窗结束时间戳
	private long popupEnd;

	@Column(name = "BIGCOUPONURL", nullable = true, columnDefinition = "varchar(255)")
	// 大额优惠券地址
	private String bigCouponUrl;

	@Column(name = "SMALLCOUPONURL", nullable = true, columnDefinition = "varchar(255)")
	// 中额优惠卷地址
	private String smallCouponUrl;

	@Column(name = "START")
	private long start;

	@Column(name = "END")
	private long end;

	@Column(name = "LATESTLOTTERY")
	private long latestLottery;
	
	// 倒计时，毫秒数
	@Transient
	private long countDown;

	@Transient
	private WishStatus status;

	@Transient
	private boolean joined;

	@Transient
	private boolean popup;

	@Transient
	private long current;

	@Transient
	private String lotteryDate;

	@Transient
	private int num;

	public SnailWish() {
	}

	public SnailWish(String startDate, String endDate, String startTime, String endTime, String suprisedUrl,
			String popupUrl, long popupStart, long popupEnd, String bigCouponUrl, String smallCouponUrl) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.suprisedUrl = suprisedUrl;
		this.popupUrl = popupUrl;
		this.popupStart = popupStart;
		this.popupEnd = popupEnd;
		this.bigCouponUrl = bigCouponUrl;
		this.smallCouponUrl = smallCouponUrl;
	}
	
	public void calculateStart() {
		LocalDate startDate = LocalDate.parse(this.getStartDate(), DateTimeFormatter.BASIC_ISO_DATE);
		LocalTime starTime = LocalTime.parse(this.getStartTime(),  DateTimeFormatter.ofPattern("HHmmss"));
		LocalDateTime startDateTime = LocalDateTime.of(startDate, starTime);
		this.start = startDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
	}
	
	public void calculateEnd() {
		LocalDate endDate = LocalDate.parse(this.getEndDate(), DateTimeFormatter.BASIC_ISO_DATE);
		LocalTime endTime = LocalTime.parse(this.getEndTime(),  DateTimeFormatter.ofPattern("HHmmss"));
		LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
		this.end = endDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
	}
	
	public static enum AwardType {
		GOODS, COUPON_BIG, COUPON_SMALL
	}

	public static enum WishStatus {
		NOT_START, START, WAIT_LOTTERY, END
	}

	public void calculateStatus() {
		String lotteryTimeStr = "120000";
		if (this.current < this.getStart()) {
			// 还未到第一次许愿时间
			this.status = WishStatus.NOT_START;
			LocalDate startDate = LocalDate.parse(this.getStartDate(), DateTimeFormatter.BASIC_ISO_DATE);
			startDate = startDate.plusDays(1);
			this.lotteryDate = startDate.format(DateTimeFormatter.BASIC_ISO_DATE);
		} else if (this.current >= this.getEnd()) {
			// 超过了最后一次的许愿时间
			LocalDate endDate = LocalDate.parse(this.getEndDate(), DateTimeFormatter.BASIC_ISO_DATE);
			endDate = endDate.plusDays(1);
			LocalTime lotteryTime = LocalTime.parse(lotteryTimeStr, DateTimeFormatter.ofPattern("HHmmss"));
			LocalDateTime lotteryDateTime = LocalDateTime.of(endDate, lotteryTime);
			long lotteryTimestamp = lotteryDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
			if (this.current >= lotteryTimestamp) {
				// 超过了最后一次开奖时间
				this.status = WishStatus.END;
			} else {
				// 未超过最后一次开奖时间
				this.status = WishStatus.WAIT_LOTTERY;
				this.lotteryDate = endDate.format(DateTimeFormatter.BASIC_ISO_DATE);
			}

		} else {
			// 在活动期间
			LocalDate todayDate = LocalDate.now();
			LocalTime starTime = LocalTime.parse(this.getStartTime(), DateTimeFormatter.ofPattern("HHmmss"));
			LocalTime endTime = LocalTime.parse(this.getEndTime(), DateTimeFormatter.ofPattern("HHmmss"));
			LocalTime lotteryTime = LocalTime.parse(lotteryTimeStr, DateTimeFormatter.ofPattern("HHmmss"));
			LocalDateTime todayStartDateTime = LocalDateTime.of(todayDate, starTime);
			LocalDateTime todayEndDateTime = LocalDateTime.of(todayDate, endTime);
			LocalDateTime todayLotteryDateTime = LocalDateTime.of(todayDate, lotteryTime);
			long todayStartTimestamp = todayStartDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
			long todayEndTimestamp = todayEndDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
			long todayLotteryTimestamp = todayLotteryDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
			if (this.current < todayLotteryTimestamp) {
				// 没到开奖时间
				this.status = WishStatus.WAIT_LOTTERY;
				this.lotteryDate = todayDate.format(DateTimeFormatter.BASIC_ISO_DATE);
			} else if (this.current < todayStartTimestamp) {
				// 没到开始许愿时间
				this.status = WishStatus.NOT_START;
				todayDate = todayDate.plusDays(1);
				this.lotteryDate = todayDate.format(DateTimeFormatter.BASIC_ISO_DATE);
			} else if (this.current < todayEndTimestamp) {
				// 许愿还未结束
				this.status = WishStatus.START;
				todayDate = todayDate.plusDays(1);
				this.lotteryDate = todayDate.format(DateTimeFormatter.BASIC_ISO_DATE);
			} else {
				// 今日许愿已经结束
				this.status = WishStatus.WAIT_LOTTERY;
				todayDate = todayDate.plusDays(1);
				this.lotteryDate = todayDate.format(DateTimeFormatter.BASIC_ISO_DATE);
			}
		}
	}

	public void calculate() {
		if (this.current >= this.getPopupStart() && this.current <= this.getPopupEnd()) {
			this.popup = true;
		}
		String lotteryTimeStr = "120000";
		if (this.current < this.getStart()) {
			// 还未到第一次许愿时间
			this.status = WishStatus.NOT_START;
			this.countDown = this.getStart() - this.current;
			LocalDate startDate = LocalDate.parse(this.getStartDate(), DateTimeFormatter.BASIC_ISO_DATE);
			startDate = startDate.plusDays(1);
			this.lotteryDate = startDate.format(DateTimeFormatter.BASIC_ISO_DATE);
			this.num = 1;
		} else if (this.current >= this.getEnd()) {
			// 超过了最后一次的许愿时间
			LocalDate endDate = LocalDate.parse(this.getEndDate(), DateTimeFormatter.BASIC_ISO_DATE);
			endDate = endDate.plusDays(1);
			LocalTime lotteryTime = LocalTime.parse(lotteryTimeStr, DateTimeFormatter.ofPattern("HHmmss"));
			LocalDateTime lotteryDateTime = LocalDateTime.of(endDate, lotteryTime);
			long lotteryTimestamp = lotteryDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
			if (this.current >= lotteryTimestamp) {
				// 超过了最后一次开奖时间
				this.status = WishStatus.END;
			} else {
				// 未超过最后一次开奖时间
				this.status = WishStatus.WAIT_LOTTERY;
				this.countDown = lotteryTimestamp - this.current;
				this.lotteryDate = endDate.format(DateTimeFormatter.BASIC_ISO_DATE);
				LocalDate startDate = LocalDate.parse(this.getStartDate(), DateTimeFormatter.BASIC_ISO_DATE);
				this.num = startDate.until(endDate).getDays();
			}

		} else {
			// 在活动期间
			LocalDate startDate = LocalDate.parse(this.getStartDate(), DateTimeFormatter.BASIC_ISO_DATE);
			LocalDate todayDate = LocalDate.now();
			LocalTime starTime = LocalTime.parse(this.getStartTime(), DateTimeFormatter.ofPattern("HHmmss"));
			LocalTime endTime = LocalTime.parse(this.getEndTime(), DateTimeFormatter.ofPattern("HHmmss"));
			LocalTime lotteryTime = LocalTime.parse(lotteryTimeStr, DateTimeFormatter.ofPattern("HHmmss"));
			LocalDateTime todayStartDateTime = LocalDateTime.of(todayDate, starTime);
			LocalDateTime todayEndDateTime = LocalDateTime.of(todayDate, endTime);
			LocalDateTime todayLotteryDateTime = LocalDateTime.of(todayDate, lotteryTime);
			long todayStartTimestamp = todayStartDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
			long todayEndTimestamp = todayEndDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
			long todayLotteryTimestamp = todayLotteryDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
			if (this.current < todayLotteryTimestamp) {
				// 没到开奖时间
				this.status = WishStatus.WAIT_LOTTERY;
				this.countDown = todayLotteryTimestamp - this.current;
				this.lotteryDate = todayDate.format(DateTimeFormatter.BASIC_ISO_DATE);
				this.num = startDate.until(todayDate).getDays();
			} else if (this.current < todayStartTimestamp) {
				// 没到开始许愿时间
				this.status = WishStatus.NOT_START;
				this.countDown = todayStartTimestamp - this.current;
				todayDate = todayDate.plusDays(1);
				this.lotteryDate = todayDate.format(DateTimeFormatter.BASIC_ISO_DATE);
				this.num = startDate.until(todayDate).getDays();
			} else if (this.current < todayEndTimestamp) {
				// 许愿还未结束
				this.status = WishStatus.START;
				this.countDown = todayEndTimestamp - this.current;
				todayDate = todayDate.plusDays(1);
				this.lotteryDate = todayDate.format(DateTimeFormatter.BASIC_ISO_DATE);
				this.num = startDate.until(todayDate).getDays();
			} else {
				// 今日许愿已经结束
				this.status = WishStatus.WAIT_LOTTERY;
				todayDate = todayDate.plusDays(1);
				LocalDateTime lottertDateTime = LocalDateTime.of(todayDate, lotteryTime);
				long lotteryTimestamp = lottertDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
				this.countDown = lotteryTimestamp - this.current;
				this.lotteryDate = todayDate.format(DateTimeFormatter.BASIC_ISO_DATE);
				this.num = startDate.until(todayDate).getDays();
			}
		}
	}

	public void joined() {
		this.joined = true;
		String lotteryTimeStr = "120000";
		LocalDate todayDate = LocalDate.now();
		LocalTime lotteryTime = LocalTime.parse(lotteryTimeStr, DateTimeFormatter.ofPattern("HHmmss"));
		this.status = WishStatus.WAIT_LOTTERY;
		todayDate = todayDate.plusDays(1);
		LocalDateTime lottertDateTime = LocalDateTime.of(todayDate, lotteryTime);
		long lotteryTimestamp = lottertDateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
		this.countDown = lotteryTimestamp - this.current;
	}


}
