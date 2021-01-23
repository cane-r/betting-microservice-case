package com.bilyoner.assignment.balanceapi.controller;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bilyoner.assignment.balanceapi.model.UpdateBalanceRequest;
import com.bilyoner.assignment.balanceapi.persistence.entity.UserBalanceEntity;
import com.bilyoner.assignment.balanceapi.service.BalanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/balances")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @PutMapping
    public void updateBalance(@Valid @RequestBody UpdateBalanceRequest updateBalanceRequest) {
        balanceService.updateBalance(updateBalanceRequest);
    }
    @GetMapping("/{userId}")
    public UserBalanceEntity getUserBalanceEntityById(@PathVariable Long userId) {
        return balanceService.getUserBalanceById(userId);
    }
}
