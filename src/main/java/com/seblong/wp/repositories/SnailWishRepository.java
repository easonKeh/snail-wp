package com.seblong.wp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.seblong.wp.entities.SnailWish;

@Repository
public interface SnailWishRepository extends JpaRepository<SnailWish, Long>{

}
