package com.bilyoner.assignment.balanceapi.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bilyoner.assignment.balanceapi.exception.BalanceApiException;
import com.bilyoner.assignment.balanceapi.model.UpdateBalanceRequest;
import com.bilyoner.assignment.balanceapi.model.enums.TransactionType;
import com.bilyoner.assignment.balanceapi.persistence.entity.UserBalanceEntity;
import com.bilyoner.assignment.balanceapi.persistence.repository.UserBalanceRepository;
import com.bilyoner.assignment.balanceapi.service.BalanceHistoryService;
import com.bilyoner.assignment.balanceapi.service.BalanceService;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {

    @InjectMocks
    private BalanceService balanceService;

    @Mock
    private UserBalanceRepository userBalanceRepository;

    @Mock
    private BalanceHistoryService balanceHistoryService;

    @Test
    public void shouldWithdraw() {
        UserBalanceEntity userBalanceEntity = UserBalanceEntity.builder()
                                                                .amount(BigDecimal.TEN)
                                                                .userId(1L)
                                                                .build();

        UpdateBalanceRequest req = UpdateBalanceRequest.builder()
                                                        .amount(BigDecimal.TEN)
                                                        .transactionId("12345")
                                                        .transactionType(TransactionType.WITHDRAW)
                                                        .userId(1L)
                                                        .build();

        ArgumentCaptor<UserBalanceEntity> captor = ArgumentCaptor.forClass(UserBalanceEntity.class);

        when(userBalanceRepository.findById(userBalanceEntity.getUserId())).thenReturn(Optional.of(userBalanceEntity));

        balanceService.updateBalance(req);

        verify(userBalanceRepository, times(1)).save(captor.capture());

        verify(balanceHistoryService, times(1)).updateBalanceHistory(req);

        assertEquals(BigDecimal.ZERO, captor.getValue().getAmount());
    }

    @Test
    public void shouldRefund() {
        UserBalanceEntity userBalanceEntity = UserBalanceEntity.builder()
                                                                .amount(BigDecimal.TEN)
                                                                .userId(1L)
                                                                .build();

        UpdateBalanceRequest req = UpdateBalanceRequest.builder()
                                                        .amount(BigDecimal.TEN)
                                                        .transactionId("12345")
                                                        .transactionType(TransactionType.REFUND)
                                                        .userId(1L)
                                                        .build();

        ArgumentCaptor<UserBalanceEntity> captor = ArgumentCaptor.forClass(UserBalanceEntity.class);

        when(userBalanceRepository.findById(userBalanceEntity.getUserId())).thenReturn(Optional.of(userBalanceEntity));

        balanceService.updateBalance(req);

        verify(userBalanceRepository, times(1)).save(captor.capture());

        verify(balanceHistoryService, times(1)).updateBalanceHistory(req);

        assertEquals(BigDecimal.TEN.multiply(BigDecimal.valueOf(2)), captor.getValue().getAmount());
    }


    @Test
    public void shouldNotWithdrawIfOutstandingBalance() {
        UserBalanceEntity userBalanceEntity = UserBalanceEntity.builder()
                                                                .amount(BigDecimal.ONE)
                                                                .userId(1L)
                                                                .build();

        UpdateBalanceRequest req = UpdateBalanceRequest.builder()
                                                        .amount(BigDecimal.TEN)
                                                        .transactionId("12345")
                                                        .transactionType(TransactionType.WITHDRAW)
                                                        .userId(1L)
                                                        .build();

        ArgumentCaptor<UserBalanceEntity> captor = ArgumentCaptor.forClass(UserBalanceEntity.class);

        when(userBalanceRepository.findById(userBalanceEntity.getUserId())).thenReturn(Optional.of(userBalanceEntity));

        assertThrows(BalanceApiException.class, () -> balanceService.updateBalance(req));

        verify(userBalanceRepository, times(0)).save(captor.capture());

        verify(balanceHistoryService, times(0)).updateBalanceHistory(req);
    }

    @Test
    public void shouldNotWithdrawForNonExistentUser() {

        UpdateBalanceRequest req = UpdateBalanceRequest.builder()
                                                        .amount(BigDecimal.TEN)
                                                        .transactionId("12345")
                                                        .transactionType(TransactionType.WITHDRAW)
                                                        .userId(2L)
                                                        .build();

        ArgumentCaptor<UserBalanceEntity> captor = ArgumentCaptor.forClass(UserBalanceEntity.class);

        when(userBalanceRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BalanceApiException.class, () -> balanceService.updateBalance(req));

        verify(userBalanceRepository, times(0)).save(captor.capture());

        verify(balanceHistoryService, times(0)).updateBalanceHistory(req);
    }
}
