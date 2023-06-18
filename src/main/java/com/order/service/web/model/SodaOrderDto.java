package com.order.service.web.model;

import com.order.service.domain.SodaOrderStatusEnum;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SodaOrderDto extends BaseItem {

    private UUID customerId;
    private String customerRef;
    private List<SodaOrderLineDto> sodaOrderLines;
    private SodaOrderStatusEnum orderStatus;
    private String orderStatusCallbackUrl;

    @Builder
    public SodaOrderDto(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate, UUID customerId, List<SodaOrderLineDto> sodaOrderLines,
                        SodaOrderStatusEnum orderStatus, String orderStatusCallbackUrl, String customerRef) {
        super(id, version, createdDate, lastModifiedDate);
        this.customerId = customerId;
        this.sodaOrderLines = sodaOrderLines;
        this.orderStatus = orderStatus;
        this.orderStatusCallbackUrl = orderStatusCallbackUrl;
        this.customerRef = customerRef;
    }

}
