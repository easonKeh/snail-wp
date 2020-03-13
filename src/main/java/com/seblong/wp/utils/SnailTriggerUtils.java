package com.seblong.wp.utils;

import java.util.Calendar;

import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class SnailTriggerUtils {

	public static Trigger getCronTrigger(String name, String description, String cron) {
		if (StringUtil.isEmpty(name)) {
			name = "auto-"+System.currentTimeMillis();
		}
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(name);
		if (!StringUtil.isEmpty(description)) {
			triggerBuilder.withDescription(description);
		}
		triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron).withMisfireHandlingInstructionDoNothing());
		return triggerBuilder.build();
	}
	
	public static Trigger getTriggerAtDate(String name, String description, long timestamp){
		if (StringUtil.isEmpty(name)) {
			name = "auto-"+System.currentTimeMillis();
		}
		TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger().withIdentity(name);
		if (!StringUtil.isEmpty(description)) {
			triggerBuilder.withDescription(description);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp);
		triggerBuilder.startAt(cal.getTime());
		return triggerBuilder.build();
	}
}
