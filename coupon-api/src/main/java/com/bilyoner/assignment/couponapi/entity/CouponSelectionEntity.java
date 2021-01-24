package com.bilyoner.assignment.couponapi.entity;

import lombok.*;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.DynamicUpdate;

@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
public class CouponSelectionEntity {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private CouponEntity couponEntity;
}
