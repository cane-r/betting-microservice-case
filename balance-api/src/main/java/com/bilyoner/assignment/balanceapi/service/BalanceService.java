package com.bilyoner.assignment.balanceapi.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bilyoner.assignment.balanceapi.exception.BalanceApiException;
import com.bilyoner.assignment.balanceapi.exception.ErrorCodeEnum;
import com.bilyoner.assignment.balanceapi.model.UpdateBalanceRequest;
import com.bilyoner.assignment.balanceapi.model.enums.TransactionType;
import com.bilyoner.assignment.balanceapi.persistence.entity.UserBalanceEntity;
import com.bilyoner.assignment.balanceapi.persistence.repository.UserBalanceRepository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Service
@Transactional(rollbackFor = Exception.class)
@AllArgsConstructor
@Getter
@Log4j2
public class BalanceService {

    private final UserBalanceRepository userBalanceRepository;
    private final BalanceHistoryService balanceHistoryService;

    public void updateBalance(UpdateBalanceRequest updateBalanceRequest) {

        UserBalanceEntity userBalanceEntity = userBalanceRepository.findById(updateBalanceRequest.getUserId())
                                                                    .orElseThrow(() -> new BalanceApiException(ErrorCodeEnum.CONTENT_NOT_FOUND_ERROR,"User not found!"));

        TransactionType transactionType = updateBalanceRequest.getTransactionType();

        BigDecimal balance = userBalanceEntity.getAmount();

        BigDecimal newBalance = null;

        final String logMessage = String.format("New balance change for the user %d with the amount %f , %s"
                                                ,updateBalanceRequest.getUserId()
                                                ,updateBalanceRequest.getAmount()
                                                ,updateBalanceRequest.getTransactionType());


        switch(transactionType) {
            case WITHDRAW :

                if(userBalanceEntity.getAmount().compareTo(updateBalanceRequest.getAmount()) < 0) {
                    throw new BalanceApiException(ErrorCodeEnum.INSUFFICIENT_BALANCE,"Your balance is insuffient!");
                }

                newBalance = balance.subtract(updateBalanceRequest.getAmount());

                userBalanceEntity.setAmount(newBalance);
                userBalanceRepository.save(userBalanceEntity);

                log.info(logMessage);
                balanceHistoryService.updateBalanceHistory(updateBalanceRequest);
                break;

            case REFUND :

                newBalance = balance.add(updateBalanceRequest.getAmount());

                userBalanceEntity.setAmount(newBalance);
                userBalanceRepository.save(userBalanceEntity);

                log.info(logMessage);
                balanceHistoryService.updateBalanceHistory(updateBalanceRequest);
                break;

            default :
                throw new RuntimeException("Unsupported transaction type!");
        }
    }

    @Transactional(readOnly = true)
    public UserBalanceEntity getUserBalanceById(Long id) {
        return userBalanceRepository.findById(id).orElseThrow(()-> new BalanceApiException(ErrorCodeEnum.CONTENT_NOT_FOUND_ERROR,"User not found!"));
    }
}
