package com.bilyoner.assignment.balanceapi.integration.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bilyoner.assignment.balanceapi.controller.BalanceController;
import com.bilyoner.assignment.balanceapi.exception.BalanceApiException;
import com.bilyoner.assignment.balanceapi.model.UpdateBalanceRequest;
import com.bilyoner.assignment.balanceapi.model.enums.TransactionType;
import com.bilyoner.assignment.balanceapi.service.BalanceService;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class BalanceControllerIT {


    @Autowired
    private BalanceController balanceController;

    @Autowired
    private BalanceService balanceService;

    @Test
    public void shouldWithdraw() {
        UpdateBalanceRequest updateBalanceRequest = UpdateBalanceRequest.builder()
                                                                        .amount(BigDecimal.TEN)
                                                                        .transactionId("12345")
                                                                        .transactionType(TransactionType.WITHDRAW)
                                                                        .userId(1L)
                                                                        .build();

        balanceController.updateBalance(updateBalanceRequest);
        assertEquals(BigDecimal.valueOf(0).setScale(2),balanceService.getUserBalanceById(1L).getAmount());
    }

    @Test
    public void shouldNotWithdraw() {
        UpdateBalanceRequest updateBalanceRequest = UpdateBalanceRequest.builder()
                                                                        .amount(BigDecimal.TEN.multiply(BigDecimal.TEN))
                                                                        .transactionId("12345")
                                                                        .transactionType(TransactionType.WITHDRAW)
                                                                        .userId(1L)
                                                                        .build();

        assertThrows(BalanceApiException.class, () -> balanceController.updateBalance(updateBalanceRequest));
    }

    @Test
    public void shouldRefund() {
        UpdateBalanceRequest updateBalanceRequest = UpdateBalanceRequest.builder()
                                                                        .amount(BigDecimal.TEN)
                                                                        .transactionId("12345")
                                                                        .transactionType(TransactionType.REFUND)
                                                                        .userId(1L)
                                                                        .build();

        balanceController.updateBalance(updateBalanceRequest);
        assertEquals(BigDecimal.valueOf(20).setScale(2),balanceService.getUserBalanceById(1L).getAmount());
    }
}
