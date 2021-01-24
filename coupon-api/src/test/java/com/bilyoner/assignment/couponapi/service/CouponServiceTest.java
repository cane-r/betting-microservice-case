package com.bilyoner.assignment.couponapi.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bilyoner.assignment.couponapi.entity.CouponEntity;
import com.bilyoner.assignment.couponapi.entity.EventEntity;
import com.bilyoner.assignment.couponapi.model.CouponCreateRequest;
import com.bilyoner.assignment.couponapi.model.CouponDTO;
import com.bilyoner.assignment.couponapi.model.enums.CouponStatusEnum;
import com.bilyoner.assignment.couponapi.repository.CouponRepository;
import com.bilyoner.assignment.couponapi.repository.CouponSelectionRepository;
import com.bilyoner.assignment.couponapi.service.validator.CouponEventValidator;

@ExtendWith(MockitoExtension.class)
public class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private EventService eventService;
    @Mock
    private CouponEventValidator couponEventValidator;
    @Mock
    private CouponSelectionRepository couponSelectionRepository;
    @Mock
    private BalanceService balanceService;

    @Test
    public void shouldGetAllCouponsByCouponStatus() {
        CouponStatusEnum status = CouponStatusEnum.CREATED;

        when(couponRepository.findByStatus(any())).thenReturn(anyList());
        couponService.getAllCouponsByCouponStatus(status);


        verify(couponRepository, times(1)).findByStatus(status);

        verifyNoMoreInteractions(couponRepository);
    }
    @Test
    public void shouldCreateCoupon(){
        CouponCreateRequest req = CouponCreateRequest.builder().eventIds(Arrays.asList(1L)).build();

        List<EventEntity> listofEvents = new ArrayList<>();

        listofEvents.add(EventEntity.builder().build());

        CouponEntity couponEntity = CouponEntity.builder().id(1L).status(CouponStatusEnum.CREATED).cost(BigDecimal.ONE).events(listofEvents).build();

        // do not raise exception .. or raise and do assertThrows(... , ...)
        doNothing().when(couponEventValidator).validateCouponCreateRequest(req);

        when(eventService.getEventsById(req.getEventIds())).thenReturn(listofEvents);

        when(couponRepository.save(any(CouponEntity.class))).thenReturn(couponEntity);

        couponService.createCoupon(req);

        verify(eventService, times(1)).getEventsById(any());

        verify(couponRepository, times(1)).save(any());
        
        verifyNoMoreInteractions(couponRepository);
    }

}
