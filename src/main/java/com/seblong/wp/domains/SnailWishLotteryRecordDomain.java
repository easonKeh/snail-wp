package com.seblong.wp.domains;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.seblong.wp.entities.SnailWish.AwardType;
import com.seblong.wp.entities.SnailWishLotteryRecord;

import lombok.Data;

@Data
public class SnailWishLotteryRecordDomain implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7288906995259810681L;
	
	private String unique;

	private String user;

	@JsonSerialize(using = ToStringSerializer.class)
	private AwardType type;

	private String name;

	private long uId;

	private String avatar;
	
	private SnailWishLotteryRecordDomain() {
	}

	public static SnailWishLotteryRecordDomain fromEntity(SnailWishLotteryRecord snailWishLotteryRecord) {
		SnailWishLotteryRecordDomain domain = new SnailWishLotteryRecordDomain();
		domain.unique = String.valueOf(snailWishLotteryRecord.getId());
		domain.user = snailWishLotteryRecord.getUser();
		domain.type = snailWishLotteryRecord.getType();
		domain.name = snailWishLotteryRecord.getName();
		domain.uId = snailWishLotteryRecord.getUId();
		domain.avatar = snailWishLotteryRecord.getAvatar();
		return domain;
	}
	

}
