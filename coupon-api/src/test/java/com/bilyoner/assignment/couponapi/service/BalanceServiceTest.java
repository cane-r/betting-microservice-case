package com.bilyoner.assignment.couponapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bilyoner.assignment.couponapi.model.UserBalanceDto;
import com.bilyoner.assignment.couponapi.model.UserBalanceUpdateRequest;
import com.bilyoner.assignment.couponapi.model.enums.TransactionType;
import com.bilyoner.assignment.couponapi.service.http.BalanceApiClient;

import net.bytebuddy.asm.Advice.Argument;

@ExtendWith(MockitoExtension.class)
public class BalanceServiceTest {
    @InjectMocks
    private BalanceService balanceService;
    @Mock
    private BalanceApiClient balanceApiClient;

    @Test
    public void shouldGetBalance() {
        Long userId = 1L;

        when(balanceApiClient.getUserBalance(anyLong())).thenReturn(new UserBalanceDto(1L, BigDecimal.TEN));

        UserBalanceDto res = balanceService.getUserBalance(userId);

        verify(balanceApiClient, times(1)).getUserBalance(userId);

        assertNotNull(res);

        assertEquals(userId, res.getUserId());

        verifyNoMoreInteractions(balanceApiClient);
    }
    @Test
    public void shouldCallUpdateBalanceCorrectly() {
        Long userId = 1L;
        UserBalanceUpdateRequest req = UserBalanceUpdateRequest.builder().amount(BigDecimal.ONE).transactionId("abc").transactionType(TransactionType.WITHDRAW).userId(userId).build();
        ArgumentCaptor<UserBalanceUpdateRequest> argumentCaptor = ArgumentCaptor.forClass(UserBalanceUpdateRequest.class);

        // or raise exception to simulate network,api call error etc
        doNothing().when(balanceApiClient).updateUserBalance(any(UserBalanceUpdateRequest.class));

        balanceService.updateBalance(req);

        verify(balanceApiClient, times(1)).updateUserBalance(argumentCaptor.capture());

        assertEquals(req, argumentCaptor.getValue());

        verifyNoMoreInteractions(balanceApiClient);
    }
}
