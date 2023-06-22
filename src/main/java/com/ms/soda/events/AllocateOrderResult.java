package com.ms.soda.events;

import com.ms.soda.model.SodaOrderDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AllocateOrderResult {

    private SodaOrderDto sodaOrderDto;
    private Boolean allocationError = false;
    private Boolean pendingInventory = false;


}
