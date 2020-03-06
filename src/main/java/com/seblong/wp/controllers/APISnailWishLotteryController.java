package com.seblong.wp.controllers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.seblong.wp.domains.SnailWishLotteryRecordDomain;
import com.seblong.wp.entities.SnailWish.AwardType;
import com.seblong.wp.entities.SnailWishLotteryRecord;
import com.seblong.wp.resource.StandardEntitiesResource;
import com.seblong.wp.resource.StandardEntityResource;
import com.seblong.wp.resource.StandardRestResource;
import com.seblong.wp.services.SnailWishLotteryRecordService;

@Controller
@RequestMapping(value = "/wish/lottery", produces = MediaType.APPLICATION_JSON_VALUE)
public class APISnailWishLotteryController {

	@Autowired
	private SnailWishLotteryRecordService snailWishLotteryRecordService;

	@GetMapping(value = "/get")
	public ResponseEntity<StandardRestResource> get(@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "date", required = true) String date) {
		SnailWishLotteryRecord record = snailWishLotteryRecordService.get(user, date);
		if (record == null) {
			return new ResponseEntity<StandardRestResource>(new StandardRestResource(404, "lottery-record-not-exist"),
					HttpStatus.OK);
		}
		return new ResponseEntity<StandardRestResource>(new StandardEntityResource<SnailWishLotteryRecordDomain>(
				SnailWishLotteryRecordDomain.fromEntity(record)), HttpStatus.OK);
	}

	@GetMapping(value = "/list")
	public ResponseEntity<StandardRestResource> list(@RequestParam(value = "type", required = true) AwardType type,
			@RequestParam(value = "date", required = true) String date,
			@RequestParam(value = "page", required = true) int page) {

		Page<SnailWishLotteryRecord> recordPage = snailWishLotteryRecordService.list(date, type, page);
		if (recordPage != null && recordPage.hasContent()) {
			List<SnailWishLotteryRecordDomain> domains = recordPage.stream()
					.map(SnailWishLotteryRecordDomain::fromEntity).collect(Collectors.toList());
			return new ResponseEntity<StandardRestResource>(
					new StandardEntitiesResource<SnailWishLotteryRecordDomain>(domains, recordPage.getTotalElements(),
							recordPage.hasPrevious(), recordPage.hasNext()),
					HttpStatus.OK);
		} else {
			return new ResponseEntity<StandardRestResource>(new StandardRestResource(404, "no-more-lottery-record"),
					HttpStatus.OK);
		}

	}
}
