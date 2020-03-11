package com.seblong.wp.controllers.manage;

import com.seblong.wp.domains.ExpressInfoDomain;
import com.seblong.wp.entities.ExpressInfo;
import com.seblong.wp.services.ExpressService;
import com.seblong.wp.utils.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Api(tags = "收货信息导出")
@RestController
@RequestMapping(value = "/manage/express", produces = {MediaType.APPLICATION_JSON_VALUE})
public class APIExpressInfoManageController {

    @Autowired
    private ExpressService expressService;

    @ApiOperation(value = "收货信息导出接口")
    @GetMapping("/export/excel")
    public void exportExpress(HttpServletResponse response){
        List<ExpressInfo> list = expressService.findAll();
        List<ExpressInfoDomain> domains = new ArrayList<>();
        list.forEach(expressInfo -> {
            domains.add(ExpressInfoDomain.fromEntity(expressInfo));
        });
        ExcelUtil.writeExcel(response, domains, "许愿池收货信息", "收货信息", new ExpressInfoDomain());
    }
}
