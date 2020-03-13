package com.seblong.wp.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.seblong.wp.services.SnailWishService;
import com.seblong.wp.utils.SpringContextUtil;

public class WishLotteryJob implements Job {

	private final static Logger LOG = LoggerFactory.getLogger(WishLotteryJob.class);

	public final static String WISH_LOTTERY_TRIGGER = "WISH_LOTTERY_TRIGGER";
	
	public final static String WISH_LOTTERY_JOB = "WISH_LOTTERY_JOB";

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		SnailWishService snailWishService = SpringContextUtil.getBean(SnailWishService.class);
		if( snailWishService != null ) {
			snailWishService.lottery();
		}else {
			LOG.error("SnailWishService not exist....................");
		}
	}

}
