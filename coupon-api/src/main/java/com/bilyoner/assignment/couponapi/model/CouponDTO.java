package com.bilyoner.assignment.couponapi.model;

import com.bilyoner.assignment.couponapi.entity.EventEntity;
import com.bilyoner.assignment.couponapi.model.enums.CouponStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuperBuilder
@EqualsAndHashCode(exclude = {"status", "cost","events","playDate"})
public class CouponDTO implements Serializable {

    private Long id;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long userId;
    private CouponStatusEnum status;
    private BigDecimal cost;
    private List<EventEntity> events;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime playDate;
}
