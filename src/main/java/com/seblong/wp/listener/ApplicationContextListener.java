package com.seblong.wp.listener;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.seblong.wp.jobs.WishLotteryJob;
import com.seblong.wp.utils.SnailTriggerUtils;

@Component
public class ApplicationContextListener implements ApplicationListener<ContextRefreshedEvent>{

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
		if( applicationContext.getParent() == null ) {
			Scheduler scheduler = applicationContext.getBean(Scheduler.class);
			
			try {
				if (!scheduler.checkExists(new TriggerKey(WishLotteryJob.WISH_LOTTERY_JOB))) {
					String cron = "0 55 11 * * ?";
					Trigger trigger = SnailTriggerUtils.getCronTrigger(WishLotteryJob.WISH_LOTTERY_JOB, "", cron);
					JobDetail jobDetail = JobBuilder.newJob(WishLotteryJob.class).build();
					scheduler.scheduleJob(jobDetail, trigger);
				}
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
	}

}
