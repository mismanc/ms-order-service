package com.order.service.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SodaOrderLineDto extends BaseItem{

    private String upc;
    private String sodaName;
    private UUID sodaId;
    private Integer orderQuantity = 0;

    @Builder
    public SodaOrderLineDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate,
                            String upc, String sodaName, UUID sodaId, Integer orderQuantity) {
        super(id, version, createdDate, lastModifiedDate);
        this.upc = upc;
        this.sodaName = sodaName;
        this.sodaId = sodaId;
        this.orderQuantity = orderQuantity;
    }

}
