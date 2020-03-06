package com.seblong.wp.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.seblong.wp.entities.SnailWish.AwardType;

import lombok.Data;

@Data
@Entity
@Table(name = "t_lottery_records", indexes = { @Index(columnList = "LOTTERYDATE,TYPE"),
		@Index(columnList = "USER,LOTTERYDATE", unique = true) })
public class SnailWishLotteryRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;

	@Column(name = "USER", nullable = false, columnDefinition = "varchar(32)")
	private String user;

	@Column(name = "TYPE", nullable = false, columnDefinition = "varchar(32)")
	@Enumerated(EnumType.STRING)
	private AwardType type;

	@Column(name = "NAME", nullable = true, columnDefinition = "varchar(255)")
	private String name;

	@Column(name = "UID")
	private long uId;

	@Column(name = "AVATAR", nullable = true, columnDefinition = "varchar(500)")
	private String avatar;

	@Column(name = "LOTTERYDATE", nullable = false, columnDefinition = "varchar(10)")
	private String lotteryDate;

	@Column(name = "CREATED")
	private long created;

	public SnailWishLotteryRecord() {
	}

	public SnailWishLotteryRecord(AwardType type, String lotteryDate, String user, String name, long uId, String avatar) {
		this.type = type;
		this.lotteryDate = lotteryDate;
		this.user = user;
		this.name = name;
		this.uId = uId;
		this.avatar = avatar;
		this.created = System.currentTimeMillis();
	}

}
