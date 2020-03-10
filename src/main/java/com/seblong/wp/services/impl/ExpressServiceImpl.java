package com.seblong.wp.services.impl;

import com.seblong.wp.entities.ExpressInfo;
import com.seblong.wp.repositories.ExpressRepository;
import com.seblong.wp.services.ExpressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpressServiceImpl implements ExpressService {

    @Autowired
    private ExpressRepository expressRepo;

    @Override
    public ExpressInfo create(String user, String name, String mobile, String province, String city, String region, String address) {
        ExpressInfo expressInfo = expressRepo.findByUser(user);
        if(expressInfo != null){
            return expressInfo;
        }
        expressInfo = new ExpressInfo();
        expressInfo.setAddress(address);
        expressInfo.setCity(city);
        expressInfo.setCreated(System.currentTimeMillis());
        expressInfo.setMobile(mobile);
        expressInfo.setName(name);
        expressInfo.setProvince(province);
        expressInfo.setRegion(region);
        expressInfo = expressRepo.save(expressInfo);
        return expressInfo;
    }
}
