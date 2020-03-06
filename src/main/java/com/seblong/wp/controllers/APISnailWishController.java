package com.seblong.wp.controllers;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.seblong.wp.domains.SnailWishDomain;
import com.seblong.wp.entities.SnailWish;
import com.seblong.wp.exceptions.ValidationException;
import com.seblong.wp.resource.StandardEntityResource;
import com.seblong.wp.resource.StandardRestResource;
import com.seblong.wp.services.SnailWishService;

@Controller
@RequestMapping(value = "/wish", produces = MediaType.APPLICATION_JSON_VALUE)
public class APISnailWishController {

	@Autowired
	private SnailWishService snailWishService;

	@GetMapping(value = "/get")
	public ResponseEntity<StandardRestResource> get(@RequestParam(value = "user", required = true) String user) {

		if (!ObjectId.isValid(user)) {
			throw new ValidationException(404, "user-not-exist");
		}
		SnailWish snailWish = snailWishService.get(user);
		if (snailWish == null) {
			throw new ValidationException(404, "snailwish-not-exist");
		}
		return new ResponseEntity<StandardRestResource>(
				new StandardEntityResource<SnailWishDomain>(SnailWishDomain.fromEntity(snailWish)), HttpStatus.OK);
	}

}
