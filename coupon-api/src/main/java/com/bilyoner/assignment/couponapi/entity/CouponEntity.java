package com.bilyoner.assignment.couponapi.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicUpdate;

import com.bilyoner.assignment.couponapi.model.enums.CouponStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
public class CouponEntity {

    @Id
    @GeneratedValue
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponStatusEnum status;

    @Column(nullable = false)
    private BigDecimal cost;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime playDate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createDate;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime updateDate;

    @ManyToMany
    @JoinTable(name = "coupon_events", joinColumns = {
        @JoinColumn(name = "coupon_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "event_id", referencedColumnName = "id")}
        //, uniqueConstraints = @UniqueConstraint(columnNames = {"colour_id", "prod_id" })
        )
    private List<EventEntity> events;
}
