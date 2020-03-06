package com.seblong.wp.controllers.manage;

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

@Controller
@RequestMapping(value = "/manage/wish", produces = MediaType.APPLICATION_JSON_VALUE)
public class APISnailWishManageController {

	@Autowired
	private SnailWishService snailWishService;

	@GetMapping(value = "/get")
	public ResponseEntity<StandardRestResource> get() {
		SnailWish snailWish = snailWishService.get();
		if (snailWish == null) {
			throw new ValidationException(404, "snailwish-not-exist");
		}
		return new ResponseEntity<StandardRestResource>(
				new StandardEntityResource<SnailWishDomain>(SnailWishDomain.fromEntity(snailWish)), HttpStatus.OK);
	}

	@PostMapping(value = "/create")
	public ResponseEntity<StandardRestResource> create(
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
		return new ResponseEntity<StandardRestResource>(
				new StandardEntityResource<SnailWishDomain>(SnailWishDomain.fromEntity(snailWish)), HttpStatus.OK);
	}

	@PostMapping(value = "/update")
	public ResponseEntity<StandardRestResource> update(@RequestParam(value = "unique", required = true) long id,
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
		return new ResponseEntity<StandardRestResource>(
				new StandardEntityResource<SnailWishDomain>(SnailWishDomain.fromEntity(snailWish)), HttpStatus.OK);
	}

	private void validate(String startDate, String endDate, String startTime, String endTime, String suprisedUrl,
			String popupUrl, long popupStart, long popupEnd, String bigCouponUrl, String smallCouponUrl) {

		LocalDate startLocalDate = null;
		try {
			startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE);
		} catch (DateTimeParseException e) {
			throw new ValidationException(400, "invalid-startDate");
		}

		LocalDate endLocalDate = null;
		try {
			endLocalDate = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE);
		} catch (DateTimeParseException e) {
			throw new ValidationException(400, "invalid-endDate");
		}

		LocalTime startLocalTime = null;
		try {
			startLocalTime = LocalTime.parse(startTime, DateTimeFormatter.ofPattern("HHmmss"));
		} catch (DateTimeParseException e) {
			throw new ValidationException(400, "invalid-startTime");
		}

		LocalTime lotteryLocalTime = LocalTime.parse("120000", DateTimeFormatter.ofPattern("HHmmss"));
		if (startLocalTime.compareTo(lotteryLocalTime) <= 0) {
			throw new ValidationException(400, "invalid-startTime");
		}
		LocalTime endLocalTime = null;
		if ("240000".equals(endTime)) {
			endTime = "235959";
		}
		try {
			endLocalTime = LocalTime.parse(endTime, DateTimeFormatter.ofPattern("HHmmss"));
		} catch (DateTimeParseException e) {
			throw new ValidationException(400, "invalid-endTime");
		}

		if (endLocalDate.compareTo(startLocalDate) <= 0) {
			throw new ValidationException(400, "invalid-endDate");
		}
		if (endLocalTime.compareTo(startLocalTime) <= 0) {
			throw new ValidationException(400, "invalid-endTime");
		}

		if (popupEnd <= popupStart) {
			throw new ValidationException(400, "invalid-popupEnd");
		}

		if (!StringUtils.isEmpty(suprisedUrl) && !RegexUtils.checkURL(suprisedUrl)) {
			throw new ValidationException(400, "invalid-suprisedUrl");
		}

		if (!StringUtils.isEmpty(popupUrl) && !RegexUtils.checkURL(popupUrl)) {
			throw new ValidationException(400, "invalid-popupUrl");
		}

		if (!StringUtils.isEmpty(smallCouponUrl) && !RegexUtils.checkURL(smallCouponUrl)) {
			throw new ValidationException(400, "invalid-smallCouponUrl");
		}

		if (!StringUtils.isEmpty(bigCouponUrl) && !RegexUtils.checkURL(bigCouponUrl)) {
			throw new ValidationException(400, "invalid-bigCouponUrl");
		}
	}

}
