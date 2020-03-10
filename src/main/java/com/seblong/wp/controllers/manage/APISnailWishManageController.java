package com.seblong.wp.controllers.manage;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.seblong.wp.domains.SnailWishDomain;
import com.seblong.wp.entities.SnailWish;
import com.seblong.wp.exceptions.ValidationException;
import com.seblong.wp.resource.StandardEntityResource;
import com.seblong.wp.resource.StandardRestResource;
import com.seblong.wp.services.SnailWishService;
import com.seblong.wp.utils.RegexUtils;

@Api("许愿池的管理后台接口")
@Controller
@RequestMapping(value = "/manage/wish", produces = MediaType.APPLICATION_JSON_VALUE)
public class APISnailWishManageController {

	@Autowired
	private SnailWishService snailWishService;

	@ApiOperation(value = "获取许愿池")
    @ApiResponses(value = {
            @ApiResponse(code = 1404, message = "snailwish-not-exist"),
            @ApiResponse(code = 200, message = "OK")
    })
	@GetMapping(value = "/get")
	public ResponseEntity<StandardEntityResource<SnailWishDomain>> get() {
		SnailWish snailWish = snailWishService.get();
		if (snailWish == null) {
			throw new ValidationException(1404, "snailwish-not-exist");
		}
		return new ResponseEntity<StandardEntityResource<SnailWishDomain>>(
				new StandardEntityResource<SnailWishDomain>(SnailWishDomain.fromEntity(snailWish)), HttpStatus.OK);
	}

	@ApiOperation(value = "创建许愿池", notes = "系统中只能存在一个许愿池")
    @ApiImplicitParams(
            value = {
            		@ApiImplicitParam(name = "startDate", value = "开始日期", dataType = "String", paramType = "form", format = "yyyyMMdd", example = "20200320", required = true),
            		@ApiImplicitParam(name = "endDate", value = "结束日期", dataType = "String", paramType = "form", format = "yyyyMMdd", example = "20200324", required = true),
            		@ApiImplicitParam(name = "startTime", value = "许愿开始时间", dataType = "String", paramType = "form", format = "HHmmss", example = "210000", required = true),
            		@ApiImplicitParam(name = "endTime", value = "许愿结束时间,注意没有240000，若为晚上12点，设置为235959", dataType = "String", paramType = "form", format = "HHmmss", example = "235959", required = true),
            		@ApiImplicitParam(name = "suprisedUrl", value = "惊喜跳转url", dataType = "String", paramType = "form", required = false),
            		@ApiImplicitParam(name = "popupUrl", value = "弹窗跳转url", dataType = "String", paramType = "form", required = false),
            		@ApiImplicitParam(name = "popupStart", value = "弹窗开始毫秒级时间戳", dataType = "Long", paramType = "form", example = "1584806400000", required = true),
            		@ApiImplicitParam(name = "popupEnd", value = "弹窗开始毫秒级时间戳", dataType = "Long", paramType = "form", example = "1584979200000", required = true),
            		@ApiImplicitParam(name = "bigCouponUrl", value = "大额优惠卷地址", dataType = "String", paramType = "form", required = false),
            		@ApiImplicitParam(name = "smallCouponUrl", value = "中额优惠卷地址", dataType = "String", paramType = "form", required = false)
            		}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 1401, message = "invalid-startDate"),
            @ApiResponse(code = 1402, message = "invalid-endDate"),
            @ApiResponse(code = 1403, message = "invalid-startTime"),
            @ApiResponse(code = 1405, message = "invalid-endTime"),
            @ApiResponse(code = 1406, message = "invalid-popupEnd"),
            @ApiResponse(code = 1407, message = "invalid-suprisedUrl"),
            @ApiResponse(code = 1408, message = "invalid-popupUrl"),
            @ApiResponse(code = 1409, message = "invalid-smallCouponUrl"),
            @ApiResponse(code = 1410, message = "invalid-bigCouponUrl"),
            @ApiResponse(code = 1411, message = "snailwish-exist"),
            @ApiResponse(code = 200, message = "OK")
    })
	@PostMapping(value = "/create")
	public ResponseEntity<StandardEntityResource<SnailWishDomain>> create(
			@RequestParam(value = "startDate", required = true) String startDate,
			@RequestParam(value = "endDate", required = true) String endDate,
			@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime,
			@RequestParam(value = "suprisedUrl", required = false, defaultValue = "") String suprisedUrl,
			@RequestParam(value = "popupUrl", required = false, defaultValue = "") String popupUrl,
			@RequestParam(value = "popupStart", required = true) long popupStart,
			@RequestParam(value = "popupEnd", required = true) long popupEnd,
			@RequestParam(value = "bigCouponUrl", required = false, defaultValue = "") String bigCouponUrl,
			@RequestParam(value = "smallCouponUrl", required = false, defaultValue = "") String smallCouponUrl) {
		validate(startDate, endDate, startTime, endTime, suprisedUrl, popupUrl, popupStart, popupEnd, bigCouponUrl,
				smallCouponUrl);
		SnailWish snailWish = snailWishService.create(startDate, endDate, startTime, endTime, suprisedUrl, popupUrl,
				popupStart, popupEnd, bigCouponUrl, smallCouponUrl);
		return new ResponseEntity<StandardEntityResource<SnailWishDomain>>(
				new StandardEntityResource<SnailWishDomain>(SnailWishDomain.fromEntity(snailWish)), HttpStatus.OK);
	}

	@ApiOperation(value = "更新许愿池")
    @ApiImplicitParams(
            value = {
            		@ApiImplicitParam(name = "unique", value = "许愿池id", dataType = "Long", paramType = "form", required = true, example = "0"),
            		@ApiImplicitParam(name = "startDate", value = "开始日期", dataType = "String", paramType = "form", format = "yyyyMMdd", example = "20200320", required = true),
            		@ApiImplicitParam(name = "endDate", value = "结束日期", dataType = "String", paramType = "form", format = "yyyyMMdd", example = "20200324", required = true),
            		@ApiImplicitParam(name = "startTime", value = "许愿开始时间", dataType = "String", paramType = "form", format = "HHmmss", example = "210000", required = true),
            		@ApiImplicitParam(name = "endTime", value = "许愿结束时间,注意没有240000，若为晚上12点，设置为235959", dataType = "String", paramType = "form", format = "HHmmss", example = "235959", required = true),
            		@ApiImplicitParam(name = "suprisedUrl", value = "惊喜跳转url", dataType = "String", paramType = "form", required = false),
            		@ApiImplicitParam(name = "popupUrl", value = "弹窗跳转url", dataType = "String", paramType = "form", required = false),
            		@ApiImplicitParam(name = "popupStart", value = "弹窗开始毫秒级时间戳", dataType = "Long", paramType = "form", example = "1584806400000", required = true),
            		@ApiImplicitParam(name = "popupEnd", value = "弹窗开始毫秒级时间戳", dataType = "Long", paramType = "form", example = "1584979200000", required = true),
            		@ApiImplicitParam(name = "bigCouponUrl", value = "大额优惠卷地址", dataType = "String", paramType = "form", required = false),
            		@ApiImplicitParam(name = "smallCouponUrl", value = "中额优惠卷地址", dataType = "String", paramType = "form", required = false)
            		}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 1401, message = "invalid-startDate"),
            @ApiResponse(code = 1402, message = "invalid-endDate"),
            @ApiResponse(code = 1403, message = "invalid-startTime"),
            @ApiResponse(code = 1404, message = "snailwish-not-exist"),
            @ApiResponse(code = 1405, message = "invalid-endTime"),
            @ApiResponse(code = 1406, message = "invalid-popupEnd"),
            @ApiResponse(code = 1407, message = "invalid-suprisedUrl"),
            @ApiResponse(code = 1408, message = "invalid-popupUrl"),
            @ApiResponse(code = 1409, message = "invalid-smallCouponUrl"),
            @ApiResponse(code = 1410, message = "invalid-bigCouponUrl"),
            @ApiResponse(code = 1411, message = "snailwish-exist"),
            @ApiResponse(code = 200, message = "OK")
    })
	@PostMapping(value = "/update")
	public ResponseEntity<StandardEntityResource<SnailWishDomain>> update(@RequestParam(value = "unique", required = true) long id,
			@RequestParam(value = "startDate", required = true) String startDate,
			@RequestParam(value = "endDate", required = true) String endDate,
			@RequestParam(value = "startTime", required = true) String startTime,
			@RequestParam(value = "endTime", required = true) String endTime,
			@RequestParam(value = "suprisedUrl", required = false, defaultValue = "") String suprisedUrl,
			@RequestParam(value = "popupUrl", required = false, defaultValue = "") String popupUrl,
			@RequestParam(value = "popupStart", required = true) long popupStart,
			@RequestParam(value = "popupEnd", required = true) long popupEnd,
			@RequestParam(value = "bigCouponUrl", required = false, defaultValue = "") String bigCouponUrl,
			@RequestParam(value = "smallCouponUrl", required = false, defaultValue = "") String smallCouponUrl) {
		validate(startDate, endDate, startTime, endTime, suprisedUrl, popupUrl, popupStart, popupEnd, bigCouponUrl,
				smallCouponUrl);
		SnailWish snailWish = snailWishService.update(id, startDate, endDate, startTime, endTime, suprisedUrl, popupUrl,
				popupStart, popupEnd, bigCouponUrl, smallCouponUrl);
		return new ResponseEntity<StandardEntityResource<SnailWishDomain>>(
				new StandardEntityResource<SnailWishDomain>(SnailWishDomain.fromEntity(snailWish)), HttpStatus.OK);
	}
	
	@ApiOperation(value = "删除许愿池")
	@ApiImplicitParam(name = "unique", value = "许愿池id", dataType = "Long", paramType = "form", example = "0",  required = true)
    @ApiResponses(value = {
            @ApiResponse(code = 1404, message = "snailwish-not-exist"),
            @ApiResponse(code = 200, message = "OK")
    })
	@PostMapping(value = "/delete")
	public ResponseEntity<StandardRestResource> delete(@RequestParam(value = "unique", required = true) long id){
		snailWishService.delete(id);
		return new ResponseEntity<StandardRestResource>(new StandardRestResource(200, "OK"), HttpStatus.OK);
	}

	private void validate(String startDate, String endDate, String startTime, String endTime, String suprisedUrl,
			String popupUrl, long popupStart, long popupEnd, String bigCouponUrl, String smallCouponUrl) {

		LocalDate startLocalDate = null;
		try {
			startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE);
		} catch (DateTimeParseException e) {
			throw new ValidationException(1401, "invalid-startDate");
		}

		LocalDate endLocalDate = null;
		try {
			endLocalDate = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE);
		} catch (DateTimeParseException e) {
			throw new ValidationException(1402, "invalid-endDate");
		}

		LocalTime startLocalTime = null;
		try {
			startLocalTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HHmmss"));
		} catch (DateTimeParseException e) {
			throw new ValidationException(1403, "invalid-startTime");
		}

		LocalTime lotteryLocalTime = LocalTime.parse("120000", DateTimeFormatter.ofPattern("HHmmss"));
		if (startLocalTime.compareTo(lotteryLocalTime) <= 0) {
			throw new ValidationException(1403, "invalid-startTime");
		}
		LocalTime endLocalTime = null;
		if ("240000".equals(endTime)) {
			endTime = "235959";
		}
		try {
			endLocalTime = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HHmmss"));
		} catch (DateTimeParseException e) {
			throw new ValidationException(1405, "invalid-endTime");
		}

		if (endLocalDate.compareTo(startLocalDate) <= 0) {
			throw new ValidationException(1402, "invalid-endDate");
		}
		if (endLocalTime.compareTo(startLocalTime) <= 0) {
			throw new ValidationException(1405, "invalid-endTime");
		}

		if (popupEnd <= popupStart) {
			throw new ValidationException(1406, "invalid-popupEnd");
		}

		if (!StringUtils.isEmpty(suprisedUrl) && !RegexUtils.checkURL(suprisedUrl)) {
			throw new ValidationException(1407, "invalid-suprisedUrl");
		}

		if (!StringUtils.isEmpty(popupUrl) && !RegexUtils.checkURL(popupUrl)) {
			throw new ValidationException(1408, "invalid-popupUrl");
		}

		if (!StringUtils.isEmpty(smallCouponUrl) && !RegexUtils.checkURL(smallCouponUrl)) {
			throw new ValidationException(1409, "invalid-smallCouponUrl");
		}

		if (!StringUtils.isEmpty(bigCouponUrl) && !RegexUtils.checkURL(bigCouponUrl)) {
			throw new ValidationException(1410, "invalid-bigCouponUrl");
		}
	}

}
