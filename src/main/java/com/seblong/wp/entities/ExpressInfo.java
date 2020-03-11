package com.seblong.wp.entities;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@ApiModel("物流信息")
@Data
@Entity
@Table(name = "t_express_info", indexes = {@Index(columnList = "USER")})
public class ExpressInfo extends BaseRowModel {

    @ApiModelProperty(value = "unique", name = "unique", dataType = "Long", example = "物流信息unique")
    @ExcelIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "user", name = "user", dataType = "String", example = "用户id")
    @ExcelProperty(value = "用户id", index = 0)
    @Column(name = "USER", columnDefinition = "varchar(50)")
    private String user;

    /**
     * 收货人姓名
     */
    @ApiModelProperty(value = "name", name = "name", dataType = "String", example = "收货人姓名")
    @ExcelProperty(value = "收货人姓名", index = 1)
    @Column(name = "NAME", columnDefinition = "varchar(20)")
    private String name;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "mobile", name = "mobile", dataType = "String", example = "收货人手机号")
    @ExcelProperty(value = "收货人联系方式", index = 2)
    @Column(name = "MOBILE", columnDefinition = "varchar(20)")
    private String mobile;

    /**
     * 省份
     */
    @ApiModelProperty(value = "province", name = "province", dataType = "String", example = "省份")
    @ExcelProperty(value = "省份", index = 3)
    @Column(name = "PROVINCE", columnDefinition = "varchar(50)")
    private String province;

    /**
     * 城市
     */
    @ApiModelProperty(value = "city", name = "city", dataType = "String", example = "城市")
    @ExcelProperty(value = "城市", index = 4)
    @Column(name = "CITY", columnDefinition = "varchar(50)")
    private String city;

    /**
     * 地区
     */
    @ApiModelProperty(value = "region", name = "region", dataType = "String", example = "地区")
    @ExcelProperty(value = "区县", index = 5)
    @Column(name = "REGION", columnDefinition = "varchar(50)")
    private String region;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "address", name = "address", dataType = "String", example = "详细地址")
    @ExcelProperty(value = "详细地址", index = 6)
    @Column(name = "ADDRESS", columnDefinition = "varchar(255)")
    private String address;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "created", name = "created", dataType = "Long", example = "创建时间")
    @ExcelProperty(value = "用户填写时间", index = 7)
    @Column(name = "CREATED")
    private Long created;

    public ExpressInfo() {
    }
}
