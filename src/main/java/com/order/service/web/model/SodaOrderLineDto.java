package com.order.service.web.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SodaOrderLineDto extends BaseItem {

    private String upc;
    private String sodaName;
    private String sodaStyle;
    private UUID sodaId;
    private Integer orderQuantity = 0;
    private BigDecimal price;

    @Builder
    public SodaOrderLineDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate,
                            String upc, String sodaName, String sodaStyle, UUID sodaId, Integer orderQuantity, BigDecimal price) {
        super(id, version, createdDate, lastModifiedDate);
        this.upc = upc;
        this.sodaName = sodaName;
        this.sodaStyle = sodaStyle;
        this.sodaId = sodaId;
        this.orderQuantity = orderQuantity;
        this.price = price;
    }

}
