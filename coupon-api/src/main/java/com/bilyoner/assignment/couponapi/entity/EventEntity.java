package com.bilyoner.assignment.couponapi.entity;

import com.bilyoner.assignment.couponapi.model.enums.EventTypeEnum;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class EventEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer mbs;

    @Enumerated
    @Column(nullable = false)
    private EventTypeEnum type;

    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime eventDate;
}
