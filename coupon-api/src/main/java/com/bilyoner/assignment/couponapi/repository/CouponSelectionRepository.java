package com.bilyoner.assignment.couponapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bilyoner.assignment.couponapi.entity.CouponSelectionEntity;

public interface CouponSelectionRepository extends JpaRepository<CouponSelectionEntity, Long> {

    @Query("select cse from CouponSelectionEntity cse join fetch cse.couponEntity where cse.couponEntity.userId = :userId")
    public List<CouponSelectionEntity> findPlayedCoupons(Long userId);

}