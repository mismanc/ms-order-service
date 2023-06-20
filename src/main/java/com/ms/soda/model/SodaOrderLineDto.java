package com.ms.soda.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SodaOrderLineDto {

    private UUID id;

    private Integer version;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ", shape=JsonFormat.Shape.STRING)
    private OffsetDateTime createdDate;

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ssZ", shape=JsonFormat.Shape.STRING)
    private OffsetDateTime lastModifiedDate;

    private String upc;
    private String sodaName;
    private String sodaStyle;
    private UUID sodaId;
    private Integer orderQuantity = 0;
    private BigDecimal price;

}
