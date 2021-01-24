package com.bilyoner.assignment.couponapi.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bilyoner.assignment.couponapi.entity.CouponEntity;
import com.bilyoner.assignment.couponapi.entity.CouponSelectionEntity;
import com.bilyoner.assignment.couponapi.entity.EventEntity;
import com.bilyoner.assignment.couponapi.exception.CouponApiException;
import com.bilyoner.assignment.couponapi.exception.ErrorCodeEnum;
import com.bilyoner.assignment.couponapi.model.CouponCreateRequest;
import com.bilyoner.assignment.couponapi.model.CouponDTO;
import com.bilyoner.assignment.couponapi.model.CouponPlayRequest;
import com.bilyoner.assignment.couponapi.model.CouponSelectionDTO;
import com.bilyoner.assignment.couponapi.model.UserBalanceDto;
import com.bilyoner.assignment.couponapi.model.UserBalanceUpdateRequest;
import com.bilyoner.assignment.couponapi.model.enums.CouponStatusEnum;
import com.bilyoner.assignment.couponapi.model.enums.TransactionType;
import com.bilyoner.assignment.couponapi.repository.CouponRepository;
import com.bilyoner.assignment.couponapi.repository.CouponSelectionRepository;
import com.bilyoner.assignment.couponapi.service.validator.CouponEventValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class CouponService {

    private final CouponRepository couponRepository;
    private final EventService eventService;
    private final CouponEventValidator couponEventValidator;
    private final CouponSelectionRepository couponSelectionRepository;
    private final BalanceService balanceService;

    @Transactional(readOnly = true)
    public List<CouponDTO> getAllCouponsByCouponStatus(CouponStatusEnum couponStatus) {
        return couponRepository.findByStatus(couponStatus).stream().map(c -> CouponDTO.builder()
        .cost(c.getCost())
        .id(c.getId())
        .status(c.getStatus())
        .events(couponRepository.findCouponByIdWithEvents(c.getId()).getEvents())
        .build()
        ).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CouponEntity getCouponById(Long couponId) {
        CouponEntity entity = couponRepository.findById(couponId).orElseThrow(() -> new CouponApiException(ErrorCodeEnum.CONTENT_NOT_FOUND_ERROR,"Coupon with id " + couponId + "not found!"));
        return entity;
    }

    public CouponDTO createCoupon(CouponCreateRequest couponCreateRequest) {
        couponEventValidator.validateCouponCreateRequest(couponCreateRequest);
        List<EventEntity> events = eventService.getEventsById(couponCreateRequest.getEventIds());
        CouponEntity couponEntity = CouponEntity.builder()
                                        .cost(BigDecimal.valueOf(5))
                                        .createDate(LocalDateTime.now())
                                        .updateDate(LocalDateTime.now())
                                        .events(events)
                                        .status(CouponStatusEnum.CREATED)
                                        .build();
        //CouponSelectionEntity couponSelectionEntity = CouponSelectionEntity.builder().couponEntity(couponEntity).build();
        CouponEntity saved = couponRepository.save(couponEntity);  
        //CouponSelectionEntity saved = couponSelectionRepository.save(couponSelectionEntity);                                        
        CouponDTO dto = CouponDTO.builder().id(saved.getId()).cost(saved.getCost()).status(saved.getStatus()).events(events).build();
        return dto;
    }

    public List<CouponDTO> playCoupons(CouponPlayRequest couponPlayRequest) {

        ///get coupon ids
        // validate
        // db update

        couponEventValidator.validateCouponPlayRequest(couponPlayRequest);

        List<CouponEntity> coupons = couponRepository.findAllById(couponPlayRequest.getCouponIds());

        if(null == coupons || coupons.size() == 0) {
            throw new CouponApiException(ErrorCodeEnum.CONTENT_NOT_FOUND_ERROR, "Coupon(s) to play could not be found!");
        }

        List<CouponDTO> res = new ArrayList<>();

        int numberOfCoupons = couponPlayRequest.getCouponIds().size();

        BigDecimal total = BigDecimal.valueOf(5).multiply(BigDecimal.valueOf(numberOfCoupons));

        //check for balance from balance api
        UserBalanceDto response = balanceService.getUserBalance(couponPlayRequest.getUserId());

        BigDecimal balance = response.getAmount();

        if(total.compareTo(balance) > 0) {
            throw new CouponApiException(ErrorCodeEnum.FIELD_VALIDATION_ERROR,"Your balance is insufficient!" );
        }

        for(CouponEntity couponEntity : coupons) {

            if(couponEntity.getUserId() != null) {
                throw new CouponApiException(ErrorCodeEnum.FIELD_VALIDATION_ERROR, "A coupon can only be played by one user!");
            }

            CouponSelectionEntity selectionEntity = CouponSelectionEntity.builder()
                                                .couponEntity(couponEntity).build();

            couponSelectionRepository.save(selectionEntity);

            couponEntity.setUserId(couponPlayRequest.getUserId());
            couponEntity.setPlayDate(LocalDateTime.now());
            couponEntity.setStatus(CouponStatusEnum.PLAYED);

            couponRepository.save(couponEntity);

            UserBalanceUpdateRequest request = UserBalanceUpdateRequest.builder()
                                                                .amount(total)
                                                                .transactionId(UUID.randomUUID().toString())
                                                                .transactionType(TransactionType.WITHDRAW)
                                                                .userId(couponPlayRequest.getUserId())
                                                                .build();
            balanceService.updateBalance(request);

            CouponDTO dto = CouponSelectionDTO.builder()
                                    .cost(BigDecimal.valueOf(5))
                                    .events(couponRepository.findCouponByIdWithEvents(couponEntity.getId()).getEvents())
                                    .playDate(LocalDateTime.now())
                                    .status(CouponStatusEnum.PLAYED)
                                    .userId(couponPlayRequest.getUserId())
                                    .id(couponEntity.getId())
                                    .selectionId(selectionEntity.getId())
                                    .build();
            res.add(dto);
        }
        return res;
    }

    public CouponDTO cancelCoupon(Long couponId) {
        CouponSelectionEntity selectionEntity = couponSelectionRepository.findById(couponId)
                        .orElseThrow(() -> new CouponApiException(ErrorCodeEnum.CONTENT_NOT_FOUND_ERROR,"Played coupon with id " + couponId + " not found!"));

        CouponEntity couponEntity = selectionEntity.getCouponEntity();

        couponEventValidator.validateCouponCancelRequest(couponEntity);

        Long userId = couponEntity.getUserId();

        couponEntity.setStatus(CouponStatusEnum.CREATED);
        couponEntity.setUpdateDate(LocalDateTime.now());
        couponEntity.setUserId(null);

        
        couponRepository.save(couponEntity);

        log.info("Cancelling coupon with the id : {} ",selectionEntity.getId());

        couponSelectionRepository.delete(selectionEntity);

        UserBalanceUpdateRequest request = UserBalanceUpdateRequest.builder()
                                                                .amount(BigDecimal.valueOf(5))
                                                                .transactionId(UUID.randomUUID().toString())
                                                                .transactionType(TransactionType.REFUND)
                                                                .userId(userId)
                                                                .build();
        balanceService.updateBalance(request);


        return CouponDTO.builder()
                        .status(CouponStatusEnum.CREATED)
                        .cost(BigDecimal.valueOf(5))
                        .events(couponRepository.findCouponByIdWithEvents(couponEntity.getId()).getEvents())
                        .id(couponEntity.getId())
                        .build();

    }

    @Transactional(readOnly = true)
    public List<CouponDTO> getPlayedCoupons(Long userId) {
        /**
         * TODO : Implement get played coupons
         */
        // use selectionrepository here
        // if its in the selection repository
        // it means its played
        // cancel coupon deletes record from selection database
        return couponSelectionRepository.findPlayedCoupons(userId)
            .stream().map(cse -> CouponSelectionDTO.builder()
            .cost(cse.getCouponEntity().getCost())
            .id(cse.getCouponEntity().getId())
            .status(cse.getCouponEntity().getStatus())
            .events(couponRepository.findCouponByIdWithEvents(cse.getCouponEntity().getId()).getEvents())
            .selectionId(cse.getId())
            .build()).collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<CouponDTO> getAllCoupons() {                
        return couponRepository.getAllCoupons().stream().map(c ->  { 
        CouponDTO dto = CouponSelectionDTO.builder().id(c.getId()).cost(c.getCost()).status(c.getStatus())
        .events(c.getEvents())
        .build();
        
        return dto;
        }).collect(Collectors.toList());
    }
}
