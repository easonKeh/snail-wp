package com.seblong.wp.domains.manage;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

import com.seblong.wp.entities.SnailWish;

import lombok.Data;

@ApiModel("SnailWish-许愿池-管理后台")
@Data
public class SnailWishDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8447002868084841494L;

	@ApiModelProperty(value = "许愿池id", name = "unique", dataType = "String")
	private String unique;

	@ApiModelProperty(value = "开始日期", name = "startDate", dataType = "String", example = "20200320")
	// 开始日期
	private String startDate;

	@ApiModelProperty(value = "结束日期", name = "endDate", dataType = "String", example = "20200324")
	// 结束日期
	private String endDate;

	@ApiModelProperty(value = "许愿开始时间", name = "startTime", dataType = "String", example = "210000")
	// 每天的开始时间
	private String startTime;

	@ApiModelProperty(value = "许愿结束时间", name = "endTime", dataType = "String", example = "235959")
	// 每天的结束时间
	private String endTime;

	@ApiModelProperty(value = "惊喜跳转url", name = "suprisedUrl", dataType = "String", allowEmptyValue = true)
	// 惊喜链接
	private String suprisedUrl;

	@ApiModelProperty(value = "弹窗跳转url", name = "popupUrl", dataType = "String", allowEmptyValue = true)
	// 弹窗链接
	private String popupUrl;

	@ApiModelProperty(value = "弹窗的开始毫秒时间戳", name = "popupStart", dataType = "Long")
	// 弹窗开始时间戳
	private long popupStart;

	@ApiModelProperty(value = "弹窗的结束毫秒时间戳", name = "popupEnd", dataType = "Long")
	// 弹窗结束时间戳
	private long popupEnd;

	@ApiModelProperty(value = "大额优惠券url", name = "bigCouponUrl", dataType = "String", allowEmptyValue = true)
	// 大额优惠券地址
	private String bigCouponUrl;

	@ApiModelProperty(value = "中额优惠卷url", name = "smallCouponUrl", dataType = "String", allowEmptyValue = true)
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
