package com.seblong.wp.config;

import com.seblong.wp.utils.SnowflakeIdWorker;


public abstract class BaseServiceConfig {

	protected static SnowflakeIdWorker snowflakeId;
	
	public static SnowflakeIdWorker getSnowflakeId() {
		return snowflakeId;
	}
	
}
