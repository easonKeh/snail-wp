package com.seblong.wp.repositories;

import com.seblong.wp.entities.ExpressInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpressRepository extends JpaRepository<ExpressInfo, Long> {
    ExpressInfo findByUser(String user);
}
