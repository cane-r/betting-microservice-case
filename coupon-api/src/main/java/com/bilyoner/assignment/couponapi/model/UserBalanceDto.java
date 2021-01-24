package com.bilyoner.assignment.couponapi.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class UserBalanceDto {
    private final Long userId;
    private final BigDecimal amount;
}