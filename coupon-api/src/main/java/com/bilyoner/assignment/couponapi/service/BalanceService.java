package com.bilyoner.assignment.couponapi.service;

import org.springframework.stereotype.Service;

import com.bilyoner.assignment.couponapi.model.UserBalanceDto;
import com.bilyoner.assignment.couponapi.model.UserBalanceUpdateRequest;
import com.bilyoner.assignment.couponapi.service.http.BalanceApiClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceApiClient client;

    public void updateBalance(UserBalanceUpdateRequest request) {
        client.updateUserBalance(request);
    }
    
    public UserBalanceDto getUserBalance(Long id) {
        return client.getUserBalance(id);
    }
}
