package com.bilyoner.assignment.balanceapi.persistence.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import com.bilyoner.assignment.balanceapi.model.enums.TransactionType;

@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class UserBalanceHistoryEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private BigDecimal changeAmount;
    private String transactionId;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private LocalDateTime timestamp;
}
