package com.bilyoner.assignment.balanceapi.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bilyoner.assignment.balanceapi.model.UpdateBalanceRequest;
import com.bilyoner.assignment.balanceapi.model.enums.TransactionType;
import com.bilyoner.assignment.balanceapi.persistence.entity.UserBalanceHistoryEntity;
import com.bilyoner.assignment.balanceapi.persistence.repository.UserBalanceHistoryRepository;
import com.bilyoner.assignment.balanceapi.service.BalanceHistoryService;

@ExtendWith(MockitoExtension.class)
public class BalanceHistoryServiceTest {

    @InjectMocks
    private BalanceHistoryService balanceHistoryService;

    @Mock
    private UserBalanceHistoryRepository userBalanceHistoryRepository;


    @Test
    public void shouldUpdateBalanceHistoryCorrectly() {

        UpdateBalanceRequest updateBalanceRequest = UpdateBalanceRequest.builder()
                                                        .amount(BigDecimal.ONE)
                                                        .transactionId("12345")
                                                        .transactionType(TransactionType.REFUND)
                                                        .userId(1L)
                                                        .build();

        ArgumentCaptor<UserBalanceHistoryEntity> captorEntity = ArgumentCaptor.forClass(UserBalanceHistoryEntity.class);

        balanceHistoryService.updateBalanceHistory(updateBalanceRequest);

        verify(userBalanceHistoryRepository, times(1)).save(captorEntity.capture());

        assertEquals(updateBalanceRequest.getAmount(),captorEntity.getValue().getChangeAmount());
        assertEquals(updateBalanceRequest.getTransactionType(),captorEntity.getValue().getTransactionType());

        verifyNoMoreInteractions(userBalanceHistoryRepository);
    }
}
