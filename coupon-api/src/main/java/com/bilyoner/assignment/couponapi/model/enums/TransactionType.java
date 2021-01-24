package com.bilyoner.assignment.couponapi.model.enums;

import lombok.Getter;

@Getter
public enum TransactionType {
    WITHDRAW("WITHDRAW"),REFUND("REFUND");

    private String transactionType;

    private TransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}
