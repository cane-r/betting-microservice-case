package com.bilyoner.assignment.couponapi.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bilyoner.assignment.couponapi.entity.CouponEntity;
import com.bilyoner.assignment.couponapi.entity.CouponSelectionEntity;
import com.bilyoner.assignment.couponapi.model.CouponCreateRequest;
import com.bilyoner.assignment.couponapi.model.CouponDTO;
import com.bilyoner.assignment.couponapi.model.CouponPlayRequest;
import com.bilyoner.assignment.couponapi.model.enums.CouponStatusEnum;
import com.bilyoner.assignment.couponapi.repository.CouponRepository;
import com.bilyoner.assignment.couponapi.repository.CouponSelectionRepository;
import com.bilyoner.assignment.couponapi.repository.EventRepository;
import com.bilyoner.assignment.couponapi.service.CouponService;
import com.fasterxml.jackson.databind.ObjectMapper;

//enable this for testing with wiremock 
//@ExtendWith({WireMockExtension.class})
@ExtendWith({SpringExtension.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
//@AutoConfigureMockMvc
public class CouponControllerIT {

    @Autowired
    private CouponController couponController;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private CouponSelectionRepository couponSelectionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // @Test
    // @Disabled
    // public void startUpWorks(ApplicationContext applicationContext ,WebApplicationContext webApplicationContext,@Autowired EventRepository r) {
    // assertTrue(r.count() > 0, "IS ZERO!!!");
    // assertNotNull(applicationContext.getBean("couponEventValidator"));
    // }
    @Test
    public void shouldCreateAndGetAllCouponsByStatus() {

        List<Long> eventIds = Arrays.asList(1L, 2L, 3L);

        CouponCreateRequest req = CouponCreateRequest.builder().eventIds(eventIds).build();

        // or use mockmvc

        CouponDTO created = couponController.createCoupon(req);

        List<CouponDTO> coupons = couponController.getAllCouponsByCouponStatus(CouponStatusEnum.CREATED);

        assertTrue(coupons.contains(created), "Failed!");
    }

    @Test
    public void shouldPlayAndListPlayedCoupons () throws Exception {                 

        List<Long> eventIds = Arrays.asList(1L, 2L, 3L);

        CouponCreateRequest req = CouponCreateRequest.builder().eventIds(eventIds).build();

        CouponDTO created = couponController.createCoupon(req);

        List<Long> coupons = new ArrayList<>();
        coupons.add(created.getId());

        CouponPlayRequest couponPlayRequest = CouponPlayRequest.builder().couponIds(coupons).userId(1L).build();

        List<CouponDTO> played = couponController.playCoupons(couponPlayRequest);

        assertNotNull(played, "Played coupon response is null!");

        assertEquals(played.get(0).getStatus(), CouponStatusEnum.PLAYED);

        assertEquals(created.getId(),couponService.getAllCoupons().get(0).getId());

        assertTrue(couponSelectionRepository.count() > 0,"Selection repository count is ZERO!");
    }
}
