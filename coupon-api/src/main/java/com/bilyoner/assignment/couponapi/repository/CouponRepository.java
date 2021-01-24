package com.bilyoner.assignment.couponapi.repository;


import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bilyoner.assignment.couponapi.entity.CouponEntity;
import com.bilyoner.assignment.couponapi.model.enums.CouponStatusEnum;

public interface CouponRepository extends JpaRepository<CouponEntity, Long> {
    
    @Query("select distinct c from CouponEntity c join fetch c.events events")
    @Cacheable("allcoupons")
    List<CouponEntity> getAllCoupons();

    @Query("select distinct c from CouponEntity c join fetch c.events where c.id = :couponId")
    @Cacheable("couponsWithEvents")
    CouponEntity findCouponByIdWithEvents(Long couponId);

    List<CouponEntity> findByStatus(CouponStatusEnum status);
}
