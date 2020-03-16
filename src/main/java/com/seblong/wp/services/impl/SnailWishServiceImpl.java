 package com.seblong.wp.services.impl;

import java.time.LocalDate;
import java.time.LocalTime;
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

import lombok.extern.log4j.Log4j2;

@Log4j2
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
		LocalDate startLocalDate = LocalDate.parse(startDate, DateTimeFormatter.BASIC_ISO_DATE);
		LocalDate lotteryDate = startLocalDate.plusDays(1);
		snailWish.setNum(1);
		snailWish.setLotteryDate(lotteryDate.format(DateTimeFormatter.BASIC_ISO_DATE));
		return snailWishRepo.save(snailWish);
	}

	@Override
	public SnailWish update(long id, String suprisedUrl, String popupUrl, long popupStart, long popupEnd,
			String bigCouponUrl, String smallCouponUrl) throws ValidationException {
		Optional<SnailWish> optional = snailWishRepo.findById(id);
		if (!optional.isPresent()) {
			throw new ValidationException(1404, "snailwish-not-exist");
		}
		SnailWish snailWish = optional.get();
		snailWish.setSuprisedUrl(suprisedUrl);
		snailWish.setPopupUrl(popupUrl);
		snailWish.setPopupStart(popupStart);
		snailWish.setPopupEnd(popupEnd);
		snailWish.setBigCouponUrl(bigCouponUrl);
		snailWish.setSmallCouponUrl(smallCouponUrl);
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
			if(snailWish.getStatus().equals(SnailWish.WishStatus.WAIT_LOTTERY)) {
				LocalTime nowTime = LocalTime.now();
				LocalTime startLotteryTime = LocalTime.parse("115500", DateTimeFormatter.ofPattern("HHmmss"));
				LocalTime lotteryTime = LocalTime.parse("120000", DateTimeFormatter.ofPattern("HHmmss"));
				if( nowTime.compareTo(startLotteryTime) >=0 && nowTime.compareTo(lotteryTime) <=0) {
					LocalDate todayDate = LocalDate.now();
					LocalDate lotteryDate = LocalDate.parse(snailWish.getLotteryDate(), DateTimeFormatter.BASIC_ISO_DATE);
					if( todayDate.compareTo(lotteryDate) < 0 ) {
						snailWish.setLotteryDate( todayDate.format(DateTimeFormatter.BASIC_ISO_DATE));
						snailWish.setNum(snailWish.getNum() - 1);
					}
				}
			}
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
			if (snailWish.getId() != id) {
				snailWish = null;
			}
		}
		if (snailWish != null) {
			snailWishRepo.delete(snailWish);
			removeSnailWish();
			clearBigUser(snailWish);
		} else {
			throw new ValidationException(1404, "snailwish-not-exist");
		}

	}

	@Override
	public boolean isAllowBig(SnailWish snailWish, String user) {
		return !isBigUser(snailWish, user);
	}

	@Override
	public void lottery() {
		log.info("开始开奖方法。。。  ");
		String lockKey = "WISH::LOTTERY:LOCK";

		RedisLock redisLock = new RedisLock(redisTemplate, lockKey);
		if (redisLock.tryLock()) {
			log.info("获取到锁，进入开奖逻辑。。。 ");
			try {
				SnailWish snailWish = get();
				if (snailWish != null && snailWish.getStatus().equals(WishStatus.WAIT_LOTTERY)) {
					log.info("符合开奖的条件，开始。。。  ");
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
								log.info("Record:  " + wishRecord.getId() + " 获得中额优惠卷");
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
							} else {
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
								List<WishRecord> wishRecords = new ArrayList<WishRecord>(allowBigRecordPage.getContent().size());
								wishRecords.addAll(allowBigRecordPage.getContent());
								List<String> bigUsers = new ArrayList<String>();
								List<WishRecord> prizeRecords = new ArrayList<WishRecord>();
								Random random = new Random();
								for (int i = 0; i < countPrize; i++) {
									int index = random.nextInt(wishRecords.size());
									WishRecord wishRecord = wishRecords.remove(index);
									log.info("Record:  " + wishRecord.getId() + " 获得实物奖品");
									wishRecord.setAwardType(AwardType.GOODS.toString());
									wishRecord.setUpdated(current);
									wishRecord.setStatus(EntityStatus.DONE.toString());
									prizeRecords.add(wishRecord);
									bigUsers.add(wishRecord.getUser());
								}
								List<WishRecord> bigRecords = new ArrayList<WishRecord>();
								for (int i = 0; i < countBig; i++) {
									int index = random.nextInt(wishRecords.size());
									WishRecord wishRecord = wishRecords.remove(index);
									log.info("Record:  " + wishRecord.getId() + " 获得大额优惠卷");
									wishRecord.setAwardType(AwardType.COUPON_BIG.toString());
									wishRecord.setUpdated(current);
									wishRecord.setStatus(EntityStatus.DONE.toString());
									bigRecords.add(wishRecord);
									bigUsers.add(wishRecord.getUser());
								}

								putBigUser(snailWish, bigUsers);

								for (int i = 0; i < wishRecords.size(); i++) {
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
								} else {
									break;
								}

							}
						}
					}
					log.info("结算逻辑结束");
					LocalDate endLocalDate = LocalDate.parse(snailWish.getEndDate(), DateTimeFormatter.BASIC_ISO_DATE);
					LocalDate endLotteryLocalDate = endLocalDate.plusDays(1);
					if (endLotteryLocalDate.compareTo(nowLocalDate) <= 0) {
						log.info("最后一天");
						snailWish.setLotteryDate("");
						snailWish.setNum(0);
					} else {
						log.info("明天继续开奖");
						nowLocalDate = nowLocalDate.plusDays(1);
						snailWish.setLotteryDate(nowLocalDate.format(DateTimeFormatter.BASIC_ISO_DATE));
						snailWish.setNum(snailWish.getNum() + 1);
					}
					removeSnailWish();
					putSnailWish(snailWish);
					snailWishRepo.save(snailWish);
				}
				
			}catch (Exception e) {
				log.error("开奖出现异常。。。。。。。。。。。。。。。");
				e.printStackTrace();
			}finally {
				redisLock.unlock();
			}
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
