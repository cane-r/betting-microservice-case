package com.bilyoner.assignment.couponapi.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CouponSelectionDTO extends CouponDTO {
    private final Long selectionId;
}
