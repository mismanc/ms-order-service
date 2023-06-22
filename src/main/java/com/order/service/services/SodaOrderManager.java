package com.order.service.services;

import com.ms.soda.model.SodaOrderDto;
import com.order.service.domain.SodaOrder;

import java.util.UUID;

public interface SodaOrderManager {

    SodaOrder newSodaOrder(SodaOrder sodaOrder);

    void processValidationResult(UUID id, Boolean isValid);

    void sodaOrderAllocationPassed(SodaOrderDto sodaOrderDto);

    void sodaOrderPendingInventory(SodaOrderDto sodaOrderDto);

    void sodaOrderAllocationFailed(SodaOrderDto sodaOrderDto);

}
