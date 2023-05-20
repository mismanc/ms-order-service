package com.order.service.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class SodaOrderLine extends BaseEntity{

    @ManyToOne
    private SodaOrder sodaOrder;

    private UUID sodaId;
    private Integer orderQuantity = 0;
    private Integer quantityAllocated = 0;

    @Builder
    public SodaOrderLine(UUID id, Long version, Timestamp createdDate, Timestamp lastModifiedDate,
                         SodaOrder sodaOrder, UUID sodaId, Integer orderQuantity,
                         Integer quantityAllocated) {
        super(id, version, createdDate, lastModifiedDate);
        this.sodaOrder = sodaOrder;
        this.sodaId = sodaId;
        this.orderQuantity = orderQuantity;
        this.quantityAllocated = quantityAllocated;
    }
}
