package com.bilyoner.assignment.balanceapi.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bilyoner.assignment.balanceapi.BalanceApiApplication;
import com.bilyoner.assignment.balanceapi.exception.GlobalExceptionHandler;
import com.bilyoner.assignment.balanceapi.model.UpdateBalanceRequest;
import com.bilyoner.assignment.balanceapi.model.enums.TransactionType;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = BalanceApiApplication.class)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BalanceApiIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void shouldListUserbalance() throws Exception {
        mockMvc.perform(get("/balances/1")
				.contentType(MediaType.APPLICATION_JSON))
				//.andDo(print())
				.andExpect(status().isOk());
    }

    @Test
    public void shouldUpdateUserbalanceIfWithdraw() throws Exception {
        UpdateBalanceRequest updateBalanceRequest = UpdateBalanceRequest.builder()
                                                                        .amount(BigDecimal.TEN)
                                                                        .transactionId("12345")
                                                                        .transactionType(TransactionType.WITHDRAW)
                                                                        .userId(1L)
                                                                        .build();


        mockMvc.perform(put("/balances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBalanceRequest)))
                //.andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(handler -> { mockMvc.perform(get("/balances/1"))
                                        .andExpect(status().isOk()).andExpect(jsonPath("$.userId").value(updateBalanceRequest.getUserId()))
                                        .andExpect(status().isOk()).andExpect(jsonPath("$.amount").value("0.0"))
                                        .andDo(print());
                });
    }

    @Test
    public void shouldUpdateUserbalanceIfRefund() throws Exception {
        UpdateBalanceRequest updateBalanceRequest = UpdateBalanceRequest.builder()
                                                                        .amount(BigDecimal.TEN)
                                                                        .transactionId("12345")
                                                                        .transactionType(TransactionType.REFUND)
                                                                        .userId(1L)
                                                                        .build();

        mockMvc.perform(put("/balances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBalanceRequest)))
                //.andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(handler -> { mockMvc.perform(get("/balances/1"))
                                        //.andDo(print())
                                        .andExpect(status().isOk()).andExpect(jsonPath("$.userId").value(updateBalanceRequest.getUserId()))
                                        .andExpect(status().isOk()).andExpect(jsonPath("$.amount").value("20.0"));
                });
    }

    @Test
    public void shouldSendBadRequestIfWithdrawOutstandingBalance() throws Exception {

        mockMvc = MockMvcBuilders.standaloneSetup(applicationContext.getBean("balanceController"))
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();

        UpdateBalanceRequest updateBalanceRequest = UpdateBalanceRequest.builder()
                                                                        .amount(BigDecimal.TEN.multiply(BigDecimal.valueOf(2)))
                                                                        .transactionId("12345")
                                                                        .transactionType(TransactionType.WITHDRAW)
                                                                        .userId(1L)
                                                                        .build();


        mockMvc.perform(put("/balances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBalanceRequest)))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient balance."))
                .andExpect(jsonPath("$.code").value("1003"));
    }

    @Test
    public void shouldSendBadRequestIfWithdrawForNonExistentUser() throws Exception {

        mockMvc = MockMvcBuilders.standaloneSetup(applicationContext.getBean("balanceController"))
                                .setControllerAdvice(new GlobalExceptionHandler())
                                .build();

        UpdateBalanceRequest updateBalanceRequest = UpdateBalanceRequest.builder()
                                                                        .amount(BigDecimal.TEN.multiply(BigDecimal.valueOf(2)))
                                                                        .transactionId("12345")
                                                                        .transactionType(TransactionType.WITHDRAW)
                                                                        .userId(10L)
                                                                        .build();


        mockMvc.perform(put("/balances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateBalanceRequest)))
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Content not found."))
                .andExpect(jsonPath("$.code").value("1002"));
    }
}
