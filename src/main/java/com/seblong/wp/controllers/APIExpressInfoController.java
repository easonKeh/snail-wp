package com.seblong.wp.controllers;

import com.seblong.wp.domains.ExpressInfoDomain;
import com.seblong.wp.entities.ExpressInfo;
import com.seblong.wp.exceptions.ValidationException;
import com.seblong.wp.resource.StandardEntitiesResource;
import com.seblong.wp.resource.StandardEntityResource;
import com.seblong.wp.resource.StandardRestResource;
import com.seblong.wp.services.ExpressService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "收货地址相关接口")
@RestController
@RequestMapping(value = "/express", produces = {MediaType.APPLICATION_JSON_VALUE})
public class APIExpressInfoController {

    @Autowired
    private ExpressService expressService;

    @ApiOperation(value = "用户填写收货信息接口")
    @ApiImplicitParams(
            value = {@ApiImplicitParam(name = "user", value = "用户id", dataType = "String", paramType = "query"),
                    @ApiImplicitParam(name = "name", value = "收货人姓名", dataType = "String", paramType = "query"),
                    @ApiImplicitParam(name = "mobile", value = "收货人姓名", dataType = "String", paramType = "query"),
                    @ApiImplicitParam(name = "province", value = "收货人姓名", dataType = "String", paramType = "query"),
                    @ApiImplicitParam(name = "city", value = "收货人姓名", dataType = "String", paramType = "query"),
                    @ApiImplicitParam(name = "region", value = "收货人姓名", dataType = "String", paramType = "query"),
                    @ApiImplicitParam(name = "address", value = "收货人姓名", dataType = "String", paramType = "query")}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK")
    })
    @PostMapping("/create")
    public Map<String, Object> create(
            @RequestParam(value = "user") String user,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "mobile") String mobile,
            @RequestParam(value = "province") String province,
            @RequestParam(value = "city") String city,
            @RequestParam(value = "region") String region,
            @RequestParam(value = "address") String address
    ){
        Map<String, Object> rMap = new HashMap<>(4);
        ExpressInfo expressInfo = expressService.create(user, name, mobile, province, city, region, address);
        rMap.put("status", 200);
        rMap.put("message", "OK");
        return rMap;
    }

    @ApiOperation(value = "获取用户收货信息接口")
    @ApiImplicitParams(
            value = {@ApiImplicitParam(name = "user", value = "用户id", dataType = "String", paramType = "query")}
    )
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK", response = ExpressInfoDomain.class)
    })
    @GetMapping("/get")
    public ResponseEntity<StandardRestResource> get(
            @RequestParam(value = "user") String user){
        ExpressInfo expressInfo = expressService.findByUser(user);
        if(expressInfo == null){
            throw new ValidationException(1404, "express-not-exist");
        }
        return new ResponseEntity<StandardRestResource>(new StandardEntityResource<ExpressInfoDomain>(ExpressInfoDomain.fromEntity(expressInfo)), HttpStatus.OK);
    }
}
