package com.seblong.wp.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@ApiModel("WishRecord-用户许愿记录")
@Data
@Entity
@Table(name = "t_wish_record", indexes = {@Index(columnList = "USER", unique = true),
        @Index(columnList = "DEVICEID", unique = true), @Index(columnList = "LOTTERYDATE")})
public class WishRecord {

    @ApiModelProperty(value = "unique", name = "unique", dataType = "Long", example = "记录unique")
    @JsonProperty(value = "unique")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "user", name = "user", dataType = "String", example = "用户id")
    @Column(name = "USER", columnDefinition = "varchar(40)")
    private String user;

    /**
     * 开奖时间
     */
    @ApiModelProperty(value = "lotteryDate", name = "lotteryDate", dataType = "String", example = "开奖时间:20200224")
    @Column(name = "LOTTERYDATE", columnDefinition = "varchar(20)")
    private String lotteryDate;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "created", name = "created", dataType = "Long", example = "创建时间戳")
    @Column(name = "CREATED")
    private Long created;

    /**
     * 更新时间
     */
    @ApiModelProperty(value = "updated", name = "updated", dataType = "Long", example = "更新时间戳")
    @Column(name = "UPDATED")
    private Long updated;

    /**
     * 状态
     */
    @ApiModelProperty(value = "status", name = "status", dataType = "String", example = "状态")
    @Column(name = "STATUS", columnDefinition = "varchar(20)")
    private String status;

    /**
     * 设备id
     */
    @ApiModelProperty(value = "deviceId", name = "deviceId", dataType = "String", example = "设备id")
    @Column(name = "DEVICEID", columnDefinition = "varchar(255)")
    private String deviceId;

    /**
     * 奖品类型
     */
    @ApiModelProperty(value = "awardType", name = "awardType", dataType = "String", example = "用户中奖状态,GOODS:实体奖品, COUPON_BIG:大额优惠券, COUPON_SMALL:小额优惠券")
    @Column(name = "AWARDTYPE")
    private String awardType;

    public WishRecord() {
    }
}
