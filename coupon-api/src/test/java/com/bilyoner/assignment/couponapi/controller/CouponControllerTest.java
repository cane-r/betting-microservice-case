package com.bilyoner.assignment.couponapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bilyoner.assignment.couponapi.entity.EventEntity;
import com.bilyoner.assignment.couponapi.exception.CouponApiException;
import com.bilyoner.assignment.couponapi.exception.ErrorCodeEnum;
import com.bilyoner.assignment.couponapi.model.CouponCreateRequest;
import com.bilyoner.assignment.couponapi.model.CouponDTO;
import com.bilyoner.assignment.couponapi.model.enums.CouponStatusEnum;
import com.bilyoner.assignment.couponapi.model.enums.EventTypeEnum;
import com.bilyoner.assignment.couponapi.service.CouponService;
import com.bilyoner.assignment.couponapi.service.validator.CouponEventValidator;

@ExtendWith(MockitoExtension.class)
public class CouponControllerTest {

    @InjectMocks
    private CouponController couponController;

    @Mock
    private CouponService couponService;

    @Mock
    private CouponEventValidator validator;

    @Test
    public void shouldListCouponsByStatus() {
        CouponStatusEnum couponStatus = CouponStatusEnum.CREATED;

        EventEntity event = EventEntity.builder()
                                .eventDate(LocalDateTime.now().plusDays(1))
                                .mbs(1).name("A-B")
                                .type(EventTypeEnum.BASKETBALL)
                                .build();

        List<EventEntity> events = new ArrayList<>();

        events.add(event);

        CouponDTO dto = CouponDTO.builder()
                            .cost(BigDecimal.valueOf(5))
                            .events(events)
                            .playDate(LocalDateTime.now())
                            .status(CouponStatusEnum.CREATED)
                            .build();

        List<CouponDTO> list = new ArrayList<>();

        list.add(dto);

        ArgumentCaptor<CouponStatusEnum> captor = ArgumentCaptor.forClass(CouponStatusEnum.class);

        when(couponService.getAllCouponsByCouponStatus(couponStatus)).thenReturn(list);

        couponController.getAllCouponsByCouponStatus(couponStatus);

        verify(couponService, times(1)).getAllCouponsByCouponStatus(captor.capture());

        assertEquals(couponStatus, captor.getValue());

        verifyNoMoreInteractions(couponService);
    }
    
    @Test
    public void shouldCreateCoupon() {
        CouponCreateRequest req = CouponCreateRequest.builder().eventIds(Arrays.asList(1L,2L)).build();

        when(couponService.createCoupon(req)).thenReturn(CouponDTO.builder().build());

        CouponDTO dto = couponController.createCoupon(req);

        verify(couponService,times(1)).createCoupon(req);
        assertNotNull(dto);
        verifyNoMoreInteractions(couponService);
    }

    @Test
    public void shouldNotCreateCoupon() {
        CouponCreateRequest req = CouponCreateRequest.builder().eventIds(Arrays.asList(1L,2L)).build();

        doThrow(new CouponApiException(ErrorCodeEnum.FIELD_VALIDATION_ERROR,"Validation Error!")).when(couponService).createCoupon(req);
        
        assertThrows(CouponApiException.class, () -> couponController.createCoupon(req));

        verifyNoMoreInteractions(couponService);
    }

    // didnt test the others as they are similar .. mock end expect .. 
}
