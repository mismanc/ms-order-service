package com.ms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.order.service.domain.SodaOrderStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SodaOrderDto {

    private UUID id;

    private Integer version;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ", shape=JsonFormat.Shape.STRING)
    private OffsetDateTime createdDate;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ", shape=JsonFormat.Shape.STRING)
    private OffsetDateTime lastModifiedDate;

    private UUID customerId;
    private String customerRef;
    private List<SodaOrderLineDto> sodaOrderLines;
    private SodaOrderStatusEnum orderStatus;
    private String orderStatusCallbackUrl;

}
