package com.seblong.wp.entities;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "t_express_info", indexes = {@Index(columnList = "USER")})
public class ExpressInfo{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
     * 用户id
     */
    @Column(name = "USER", columnDefinition = "varchar(50)")
    private String user;

    /**
     * 收货人姓名
     */
    @Column(name = "NAME", columnDefinition = "varchar(20)")
    private String name;

    /**
     * 手机号
     */
    @Column(name = "MOBILE", columnDefinition = "varchar(20)")
    private String mobile;

    /**
     * 省份
     */
    @Column(name = "PROVINCE", columnDefinition = "varchar(50)")
    private String province;

    /**
     * 城市
     */
    @Column(name = "CITY", columnDefinition = "varchar(50)")
    private String city;

    /**
     * 地区
     */
    @Column(name = "REGION", columnDefinition = "varchar(50)")
    private String region;

    /**
     * 详细地址
     */
    @Column(name = "ADDRESS", columnDefinition = "varchar(255)")
    private String address;

    /**
     * 创建时间
     */
    @Column(name = "CREATED")
    private Long created;

    public ExpressInfo() {
    }
}
