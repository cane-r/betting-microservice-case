package com.bilyoner.assignment.balanceapi.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.bilyoner.assignment.balanceapi.controller.BalanceController;
import com.bilyoner.assignment.balanceapi.exception.BalanceApiException;
import com.bilyoner.assignment.balanceapi.exception.ErrorCodeEnum;
import com.bilyoner.assignment.balanceapi.exception.GlobalExceptionHandler;
import com.bilyoner.assignment.balanceapi.model.UpdateBalanceRequest;
import com.bilyoner.assignment.balanceapi.model.enums.TransactionType;
import com.bilyoner.assignment.balanceapi.persistence.entity.UserBalanceEntity;
import com.bilyoner.assignment.balanceapi.service.BalanceService;
import com.fasterxml.jackson.databind.ObjectMapper;


@ExtendWith(MockitoExtension.class)
public class BalanceControllerTest {

   @Mock
   private BalanceService balanceService;

   @InjectMocks
   private BalanceController balanceController;

   @Test
   public void controllerShouldHandleException() throws Exception{
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(balanceController).setControllerAdvice(new GlobalExceptionHandler()).build();

        ObjectMapper objectMapper = new ObjectMapper();

        UpdateBalanceRequest updateBalanceRequest = UpdateBalanceRequest.builder()
                                                                        .amount(BigDecimal.TEN)
                                                                        .transactionId("12345")
                                                                        .transactionType(TransactionType.REFUND)
                                                                        .userId(1L)
                                                                        .build();

        String body = objectMapper.writeValueAsString(updateBalanceRequest);

        doThrow(new BalanceApiException(ErrorCodeEnum.INSUFFICIENT_BALANCE,"Insufficient Balance!")).doNothing().when(balanceService).updateBalance(any(UpdateBalanceRequest.class));

        mockMvc.perform(put("/balances")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                //.andDo(print())
                .andExpect(status().is4xxClientError());
   }

    @Test
   public void controllerShouldGetBalance() throws Exception{

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(balanceController).build();

        UserBalanceEntity userBalanceEntity = UserBalanceEntity.builder()
                                                                .amount(BigDecimal.TEN)
                                                                .userId(1L)
                                                                .build();

        when(balanceService.getUserBalanceById(anyLong())).thenReturn(userBalanceEntity);

        mockMvc.perform(get("/balances/1")
                .contentType(MediaType.APPLICATION_JSON))
                //.andDo(print())
                .andExpect(status().isOk()).andExpect(jsonPath("$.userId").value(userBalanceEntity.getUserId()))
                .andExpect(status().isOk()).andExpect(jsonPath("$.amount").value(userBalanceEntity.getAmount()));
   }

}
