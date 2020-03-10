package com.seblong.wp.services;

import com.seblong.wp.entities.ExpressInfo;

public interface ExpressService {
    ExpressInfo create(String user, String name, String mobile, String province, String city, String region, String address);
}
