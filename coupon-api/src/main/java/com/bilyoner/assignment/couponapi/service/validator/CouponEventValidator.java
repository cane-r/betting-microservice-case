package com.bilyoner.assignment.couponapi.service.validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.bilyoner.assignment.couponapi.entity.CouponEntity;
import com.bilyoner.assignment.couponapi.entity.EventEntity;
import com.bilyoner.assignment.couponapi.exception.CouponApiException;
import com.bilyoner.assignment.couponapi.exception.ErrorCodeEnum;
import com.bilyoner.assignment.couponapi.model.CouponCreateRequest;
import com.bilyoner.assignment.couponapi.model.CouponPlayRequest;
import com.bilyoner.assignment.couponapi.model.enums.EventTypeEnum;
import com.bilyoner.assignment.couponapi.service.CouponService;
import com.bilyoner.assignment.couponapi.service.EventService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CouponEventValidator {
    private final EventService eventService;
    //private final CouponService couponService;

    public void validateCouponPlayRequest(CouponPlayRequest couponCreateRequest) {
        if(null == couponCreateRequest) {
            throw new NullPointerException("Events to validate is null!");
        }
        List<Long> couponIds = couponCreateRequest.getCouponIds();

        List<EventEntity> events = eventService.getEventsById(couponIds);
        //in case its played later and it has expired matches.
        validateGameDates(events);

    }

    public void validateCouponCreateRequest(CouponCreateRequest couponCreateRequest) {
        if(null == couponCreateRequest) {
            throw new NullPointerException("Events to validate is null!");
        }
        List<Long> eventIds = couponCreateRequest.getEventIds();
        validateGameExistence(eventIds);
        validateDuplicateGameExists(eventIds);
        List<EventEntity> events = eventService.getEventsById(eventIds);
        validateMBSRule(events);
        validateGameTypes(events);
        validateGameDates(events);
    }

    private void validateDuplicateGameExists(List<Long> eventIds) {
        if (eventIds.stream().collect(Collectors.toSet()).size() != eventIds.size()) {
            throw new CouponApiException(ErrorCodeEnum.FIELD_VALIDATION_ERROR, "Coupon can not have duplicate matches!");        }
    }

    private void validateGameExistence(List<Long> events) {
        events.forEach(e-> eventService.getEventById(e));
    }

    private void validateMBSRule(List<EventEntity> events) {
        ErrorCodeEnum error = ErrorCodeEnum.FIELD_VALIDATION_ERROR;

        int maxMbs = events.stream()
                            .mapToInt(EventEntity::getMbs)
                            .min()
                            .orElseThrow(()-> new CouponApiException(error));
        
        if(events.size() < maxMbs) {
            throw new CouponApiException(error,"Coupon does not comply with MBS rule!");
        }
    }

    private void validateGameTypes(List<EventEntity> events) {
        boolean isFootballExists = false,isTennisExists = false;

        for(EventEntity entity : events) {
            if(entity.getType().equals(EventTypeEnum.FOOTBALL)) {
                isFootballExists = true;
            }
            if(entity.getType().equals(EventTypeEnum.TENNIS)) {
                isTennisExists = true;
            }
            if(isFootballExists && isTennisExists) {
                throw new CouponApiException(ErrorCodeEnum.FIELD_VALIDATION_ERROR,"Tennis and football matches can not co-exist in a coupon!");
            }
        }
    }

    private void validateGameDates(List<EventEntity> events) {
        LocalDateTime now = LocalDateTime.now();

        boolean isExpiredMathExists = events.stream().anyMatch(ev -> now.isAfter(ev.getEventDate()));

        if(isExpiredMathExists) {
            throw new CouponApiException(ErrorCodeEnum.FIELD_VALIDATION_ERROR,"Coupon can not have expired matches!");
        }
    }

    public void validateCouponCancelRequest(CouponEntity couponEntity) {
        LocalDateTime couponPlayTime = couponEntity.getPlayDate();

        if(null == couponPlayTime) {
            throw new CouponApiException(ErrorCodeEnum.FIELD_VALIDATION_ERROR, "Coupon to cancel does not have a play date!");
        }
        LocalDateTime limit = couponPlayTime.plusMinutes(5);

        if(LocalDateTime.now().isAfter(limit)) {
            throw new CouponApiException(ErrorCodeEnum.FIELD_VALIDATION_ERROR, "Coupons can be canceled within 5 minutes after playing!");
        }
    }
    
}
