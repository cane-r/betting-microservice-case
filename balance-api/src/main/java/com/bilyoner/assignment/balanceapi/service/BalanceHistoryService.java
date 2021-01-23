package com.bilyoner.assignment.balanceapi.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bilyoner.assignment.balanceapi.model.UpdateBalanceRequest;
import com.bilyoner.assignment.balanceapi.persistence.entity.UserBalanceHistoryEntity;
import com.bilyoner.assignment.balanceapi.persistence.repository.UserBalanceHistoryRepository;

import lombok.AllArgsConstructor;

@Transactional(rollbackFor = Exception.class)
@Service
@AllArgsConstructor
public class BalanceHistoryService {

    private final UserBalanceHistoryRepository userBalanceHistoryRepository;

    public void updateBalanceHistory(UpdateBalanceRequest dto) {
        final UserBalanceHistoryEntity userBalanceEntity = UserBalanceHistoryEntity.builder()
                                                        .userId(dto.getUserId())
                                                        .changeAmount(dto.getAmount())
                                                        .timestamp(LocalDateTime.now())
                                                        .transactionType(dto.getTransactionType())
                                                        .transactionId(dto.getTransactionId())
                                                        .build();

        userBalanceHistoryRepository.save(userBalanceEntity);
    }

}
