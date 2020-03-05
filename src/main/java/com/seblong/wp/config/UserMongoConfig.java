package com.seblong.wp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.seblong.wp.properties.AbstractMongoProperties;

@Configuration
@ConfigurationProperties("snail.wp.mongodb.user")
@EnableMongoRepositories(basePackages = {"com.seblong.wp.repositories.mongodb"}, mongoTemplateRef =  "userMongoTemplate")
public class UserMongoConfig extends AbstractMongoProperties{

	@Override
	@Bean(name = "userMongoTemplate")
	public MongoTemplate getMongoTemplate() {
		return new MongoTemplate(mongoDbFactory());
	}

}
