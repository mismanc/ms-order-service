package com.order.service.services;

import com.order.service.domain.SodaOrder;

import java.util.UUID;

public interface SodaOrderManager {

    SodaOrder newSodaOrder(SodaOrder sodaOrder);

    void processValidationResult(UUID id, Boolean isValid);

}
