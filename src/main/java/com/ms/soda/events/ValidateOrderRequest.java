package com.ms.soda.events;

import com.ms.soda.model.SodaOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidateOrderRequest {

    private SodaOrderDto sodaOrderDto;


}
