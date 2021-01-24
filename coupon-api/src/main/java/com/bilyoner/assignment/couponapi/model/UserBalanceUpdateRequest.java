package com.bilyoner.assignment.couponapi.model;

import java.math.BigDecimal;

import com.bilyoner.assignment.couponapi.model.enums.TransactionType;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Builder
public class UserBalanceUpdateRequest {
    private final Long userId;
    private final BigDecimal amount;
    private final String transactionId;
    private final TransactionType transactionType;
}
