package com.seblong.wp.services.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.seblong.wp.entities.SnailWish;
import com.seblong.wp.entities.SnailWish.AwardType;
import com.seblong.wp.entities.SnailWish.WishStatus;
import com.seblong.wp.entities.WishRecord;
import com.seblong.wp.enums.EntityStatus;
import com.seblong.wp.exceptions.ValidationException;
import com.seblong.wp.repositories.SnailWishRepository;
import com.seblong.wp.repositories.WishRecordRepository;
import com.seblong.wp.services.SnailWishLotteryRecordService;
import com.seblong.wp.services.SnailWishService;
import com.seblong.wp.utils.RedisLock;

@Service
public class SnailWishServiceImpl implements SnailWishService {

	@Autowired
	private SnailWishRepository snailWishRepo;

	@Autowired
	private WishRecordRepository wishRecordRepo;

	@Autowired
	private SnailWishLotteryRecordService snailWishLotteryRecordService;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private final static String key = "SNAIL::WISH";

	@Override
	public SnailWish create(String startDate, String endDate, String startTime, String endTime, String suprisedUrl,
			String popupUrl, long popupStart, long popupEnd, String bigCouponUrl, String smallCouponUrl)
			throws ValidationException {
		if (get() != null) {
			throw new ValidationException(1411, "snailwish-exist");
		}

		SnailWish snailWish = new SnailWish(startDate, endDate, startTime, endTime, suprisedUrl, popupUrl, popupStart,
				popupEnd, bigCouponUrl, smallCouponUrl);
		snailWish.calculateStart();
		snailWish.calculateEnd();
		return snailWishRepo.save(snailWish);
	}

	@Override
	public SnailWish update(long id, String startDate, String endDate, String startTime, String endTime,
			String suprisedUrl, String popupUrl, long popupStart, long popupEnd, String bigCouponUrl,
			String smallCouponUrl) throws ValidationException {
		Optional<SnailWish> optional = snailWishRepo.findById(id);
		if (!optional.isPresent()) {
			throw new ValidationException(1404, "snailwish-not-exist");
		}
		SnailWish snailWish = optional.get();
		snailWish.setStartDate(startDate);
		snailWish.setEndDate(endDate);
		snailWish.setStartTime(startTime);
		snailWish.setEndTime(endTime);
		snailWish.setSuprisedUrl(suprisedUrl);
		snailWish.setPopupUrl(popupUrl);
		snailWish.setPopupStart(popupStart);
		snailWish.setPopupEnd(popupEnd);
		snailWish.setBigCouponUrl(bigCouponUrl);
		snailWish.setSmallCouponUrl(smallCouponUrl);
		snailWish.calculateStart();
		snailWish.calculateEnd();
		removeSnailWish();
		return snailWishRepo.save(snailWish);
	}

	@Override
	public SnailWish get() {
		SnailWish snailWish = loadSnailWish();
		if (snailWish == null) {
			List<SnailWish> snailWishes = snailWishRepo.findAll();
			if (!CollectionUtils.isEmpty(snailWishes)) {
				snailWish = snailWishes.get(0);
				putSnailWish(snailWish);
				snailWish.calculateStatus();
			}
		} else {
			snailWish.calculateStatus();
		}
		return snailWish;
	}

	@Override
	public SnailWish get(String user) {
		SnailWish snailWish = loadSnailWish();
		if (snailWish == null) {
			List<SnailWish> snailWishes = snailWishRepo.findAll();
			if (!CollectionUtils.isEmpty(snailWishes)) {
				snailWish = snailWishes.get(0);
				putSnailWish(snailWish);
			}
		}
		if (snailWish != null) {
			snailWish.calculate();
			// 判断用户是否已经参加
			if (wishRecordRepo.countByUserAndLotteryDate(user, snailWish.getLotteryDate()) > 0) {
				// 若已经参加则调用snailWish.joined();
				snailWish.joined();
			}
		}
		return snailWish;
	}

	@Override
	public void delete(long id) {
		List<SnailWish> snailWishes = snailWishRepo.findAll();
		SnailWish snailWish = null;
		if (!CollectionUtils.isEmpty(snailWishes)) {
			snailWish = snailWishes.get(0);
			if( snailWish.getId() != id ) {
				snailWish = null;
			}
		}
		if( snailWish != null ) {
			snailWishRepo.delete(snailWish);
			removeSnailWish();
			clearBigUser(snailWish);
		}else {
			throw new ValidationException(1404, "snailwish-not-exist");
		}

	}

	@Override
	public boolean isAllowBig(SnailWish snailWish, String user) {
		return !isBigUser(snailWish, user);
	}

	@Scheduled(cron = "0 55 11 * * ?")
	@Override
	public void lottery() {
		String lockKey = "WISH::LOTTERY:LOCK";

		RedisLock redisLock = new RedisLock(redisTemplate, lockKey);
		if (redisLock.tryLock()) {
			SnailWish snailWish = get();
			if (snailWish != null && snailWish.getStatus().equals(WishStatus.WAIT_LOTTERY)) {
				LocalDate nowLocalDate = LocalDate.now();
				String nowLocalDateStr = nowLocalDate.format(DateTimeFormatter.BASIC_ISO_DATE);
				if (nowLocalDateStr.equals(snailWish.getLotteryDate())) {
					// 80,200
					// 取出不允许奖品的记录
					int page = 0, size = 128;
					Sort sort = Sort.by("id");
					Pageable notAllowBigPageable = PageRequest.of(page, size, sort);
					Page<WishRecord> notAllowBigRecordPage = wishRecordRepo
							.findByLotteryDateAndAllowBig(nowLocalDateStr, false, notAllowBigPageable);
					long current = System.currentTimeMillis();
					while (notAllowBigRecordPage != null && notAllowBigRecordPage.hasContent()) {
						for (WishRecord wishRecord : notAllowBigRecordPage) {
							wishRecord.setAwardType(AwardType.COUPON_SMALL.toString());
							wishRecord.setUpdated(current);
							wishRecord.setStatus(EntityStatus.DONE.toString());
						}
						snailWishLotteryRecordService.create(notAllowBigRecordPage.getContent());
						wishRecordRepo.saveAll(notAllowBigRecordPage.getContent());
						if (notAllowBigRecordPage.hasNext()) {
							notAllowBigPageable = PageRequest.of(++page, size, sort);
							notAllowBigRecordPage = wishRecordRepo.findByLotteryDateAndAllowBig(nowLocalDateStr, false,
									notAllowBigPageable);
						}else {
							break;
						}
					}
					// 取出允许奖品的记录
					int prize = 80, prizeRemain = 80;
					int big = 200, bigRemain = 200;
					long allowBigTotal = wishRecordRepo.countByLotteryDateAndAllowBig(nowLocalDateStr, true);
					if (allowBigTotal > 0) {
						page = 0;
						Pageable allowBigPageable = PageRequest.of(page, size, sort);
						Page<WishRecord> allowBigRecordPage = wishRecordRepo
								.findByLotteryDateAndAllowBig(nowLocalDateStr, true, allowBigPageable);
						while (allowBigRecordPage != null && allowBigRecordPage.hasContent()) {
							int count = allowBigRecordPage.getNumberOfElements();
							double rate = 0;
							if (count >= allowBigTotal) {
								rate = 1;
							} else {
								rate = count / allowBigTotal;
							}

							int countPrize = new Double(Math.ceil(prize * rate)).intValue();
							if (count > countPrize) {
								count -= countPrize;
							} else {
								countPrize = count;
								count = 0;
							}
							int countBig = new Double(Math.ceil(big * rate)).intValue();
							if (count > countBig) {
								count -= countBig;
							} else {
								countBig = count;
								count = 0;
							}
							if (prizeRemain > 0 && prizeRemain > countPrize) {
								prizeRemain -= countPrize;
							} else {
								countPrize = prizeRemain;
								prizeRemain = 0;
							}
							if (bigRemain > 0 && bigRemain > countBig) {
								bigRemain -= countBig;
							} else {
								countBig = bigRemain;
								bigRemain = 0;
							}
							List<WishRecord> wishRecords = allowBigRecordPage.getContent();
							List<String> bigUsers = new ArrayList<String>();
							List<WishRecord> prizeRecords = new ArrayList<WishRecord>();
							Random random = new Random();
							for (int i = 0; i < countPrize; i++) {
								int index = random.nextInt(wishRecords.size());
								WishRecord wishRecord = wishRecords.remove(index);
								wishRecord.setAwardType(AwardType.GOODS.toString());
								wishRecord.setUpdated(current);
								wishRecord.setStatus(EntityStatus.DONE.toString());
								prizeRecords.add(wishRecord);
								bigUsers.add(wishRecord.getUser());
							}
							List<WishRecord> bigRecords = allowBigRecordPage.getContent();
							for( int i = 0; i < countBig ; i++) {
								int index = random.nextInt(wishRecords.size());
								WishRecord wishRecord = wishRecords.remove(index);
								wishRecord.setAwardType(AwardType.COUPON_BIG.toString());
								wishRecord.setUpdated(current);
								wishRecord.setStatus(EntityStatus.DONE.toString());
								bigRecords.add(wishRecord);
								bigUsers.add(wishRecord.getUser());
							}
							
							putBigUser(snailWish, bigUsers);
							
							for( int i = 0; i < wishRecords.size() ; i++) {
								WishRecord wishRecord = wishRecords.get(i);
								wishRecord.setAwardType(AwardType.COUPON_SMALL.toString());
								wishRecord.setUpdated(current);
								wishRecord.setStatus(EntityStatus.DONE.toString());
							}
							wishRecords.addAll(prizeRecords);
							wishRecords.addAll(bigRecords);
							
							snailWishLotteryRecordService.create(wishRecords);
							wishRecordRepo.saveAll(wishRecords);
							if (allowBigRecordPage.hasNext()) {
								allowBigPageable = PageRequest.of(++page, size, sort);
								allowBigRecordPage = wishRecordRepo.findByLotteryDateAndAllowBig(nowLocalDateStr, true,
										allowBigPageable);
							}else {
								break;
							}
							
						}
					}
				}
				LocalDate endLocalDate = LocalDate.parse(snailWish.getEndDate(), DateTimeFormatter.BASIC_ISO_DATE);
				LocalDate endLotteryLocalDate = endLocalDate.plusDays(1);
				if( endLotteryLocalDate.compareTo(nowLocalDate) <= 0 ) {
					snailWish.setLotteryDate("");
					snailWish.setNum(0);
				}else {
					nowLocalDate = nowLocalDate.plusDays(1);
					snailWish.setLotteryDate(nowLocalDate.format(DateTimeFormatter.BASIC_ISO_DATE));
					snailWish.setNum(snailWish.getNum() + 1);
				}
				snailWishRepo.save(snailWish);
			}

			redisLock.unlock();
		}

	}

	private void putSnailWish(SnailWish snailWish) {
		redisTemplate.boundValueOps(key).set(snailWish);
	}

	private SnailWish loadSnailWish() {
		Object result = redisTemplate.boundValueOps(key).get();
		if (result != null) {
			return (SnailWish) result;
		}
		return null;
	}

	private void removeSnailWish() {
		redisTemplate.delete(key);
	}

	private void putBigUser(SnailWish snailWish, List<String> users) {
		String key = "SNAIL::WISH::BIG::" + snailWish.getId();
		redisTemplate.boundSetOps(key).add(users.toArray());
	}

	private boolean isBigUser(SnailWish snailWish, String user) {
		String key = "SNAIL::WISH::BIG::" + snailWish.getId();
		return redisTemplate.boundSetOps(key).isMember(user);
	}

	private void clearBigUser(SnailWish snailWish) {
		String key = "SNAIL::WISH::BIG::" + snailWish.getId();
		redisTemplate.delete(key);
	}
}
