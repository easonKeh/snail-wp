package com.seblong.wp.entities;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@ApiModel("物流信息")
@Data
@Entity
@Table(name = "t_express_info", indexes = {@Index(columnList = "USER")})
public class ExpressInfo {

    @ApiModelProperty(value = "unique", name = "unique", dataType = "Long", example = "物流信息unique")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "user", name = "user", dataType = "String", example = "用户id")
    @Column(name = "USER", columnDefinition = "varchar(50)")
    private String user;

    /**
     * 收货人姓名
     */
    @ApiModelProperty(value = "name", name = "name", dataType = "String", example = "收货人姓名")
    @Column(name = "NAME", columnDefinition = "varchar(20)")
    private String name;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "mobile", name = "mobile", dataType = "String", example = "收货人手机号")
    @Column(name = "MOBILE", columnDefinition = "varchar(20)")
    private String mobile;

    /**
     * 省份
     */
    @ApiModelProperty(value = "province", name = "province", dataType = "String", example = "省份")
    @Column(name = "PROVINCE", columnDefinition = "varchar(50)")
    private String province;

    /**
     * 城市
     */
    @ApiModelProperty(value = "city", name = "city", dataType = "String", example = "城市")
    @Column(name = "CITY", columnDefinition = "varchar(50)")
    private String city;

    /**
     * 地区
     */
    @ApiModelProperty(value = "region", name = "region", dataType = "String", example = "地区")
    @Column(name = "REGION", columnDefinition = "varchar(50)")
    private String region;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "address", name = "address", dataType = "String", example = "详细地址")
    @Column(name = "ADDRESS", columnDefinition = "varchar(255)")
    private String address;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "created", name = "created", dataType = "Long", example = "创建时间")
    @Column(name = "CREATED")
    private Long created;

    public ExpressInfo() {
    }
}
