package com.bilyoner.assignment.couponapi.service.http;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bilyoner.assignment.couponapi.model.UserBalanceDto;
import com.bilyoner.assignment.couponapi.model.UserBalanceUpdateRequest;

@FeignClient(value = "balanceClient", url = "http://localhost:9090/balances/")
@Component
public interface BalanceApiClient {

    @GetMapping("{userId}")
    public UserBalanceDto getUserBalance(@PathVariable Long userId);

    @PutMapping
    public void updateUserBalance(@RequestBody UserBalanceUpdateRequest req);
}
