package com.seblong.wp.domains;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import com.seblong.wp.entities.ExpressInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;

@ApiModel("物流信息")
@Data
public class ExpressInfoDomain extends BaseRowModel {

    @ApiModelProperty(value = "unique", name = "unique", dataType = "Long", example = "物流信息unique")
    @ExcelIgnore
    private Long unique;

    /**
     * 用户id
     */
    @ApiModelProperty(value = "user", name = "user", dataType = "String", example = "用户id")
    @ExcelProperty(value = "用户id", index = 0)
    private String user;

    /**
     * 收货人姓名
     */
    @ApiModelProperty(value = "name", name = "name", dataType = "String", example = "收货人姓名")
    @ExcelProperty(value = "收货人姓名", index = 1)
    private String name;

    /**
     * 手机号
     */
    @ApiModelProperty(value = "mobile", name = "mobile", dataType = "String", example = "收货人手机号")
    @ExcelProperty(value = "收货人联系方式", index = 2)
    private String mobile;

    /**
     * 省份
     */
    @ApiModelProperty(value = "province", name = "province", dataType = "String", example = "省份")
    @ExcelProperty(value = "省份", index = 3)
    private String province;

    /**
     * 城市
     */
    @ApiModelProperty(value = "city", name = "city", dataType = "String", example = "城市")
    @ExcelProperty(value = "城市", index = 4)
    private String city;

    /**
     * 地区
     */
    @ApiModelProperty(value = "region", name = "region", dataType = "String", example = "地区")
    @ExcelProperty(value = "区县", index = 5)
    private String region;

    /**
     * 详细地址
     */
    @ApiModelProperty(value = "address", name = "address", dataType = "String", example = "详细地址")
    @ExcelProperty(value = "详细地址", index = 6)
    private String address;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "created", name = "created", dataType = "Long", example = "创建时间")
    @ExcelProperty(value = "用户填写时间", index = 7)
    private Long created;

    public ExpressInfoDomain() {
    }

    public static ExpressInfoDomain fromEntity(ExpressInfo entity){
        ExpressInfoDomain domain = new ExpressInfoDomain();
        domain.address = entity.getAddress();
        domain.unique = entity.getId();
        domain.city = entity.getCity();
        domain.created = entity.getCreated();
        domain.mobile = entity.getMobile();
        domain.name = entity.getName();
        domain.province = entity.getProvince();
        domain.region = entity.getRegion();
        domain.user = entity.getUser();
        return domain;
    }
}
