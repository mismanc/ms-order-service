package com.order.service.services;

import com.ms.soda.model.SodaOrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface SodaOrderService {

    Page<SodaOrderDto> listOrders(UUID customerId, Pageable pageable);

    SodaOrderDto placeOrder(UUID customerId, SodaOrderDto sodaOrderDto);

    SodaOrderDto getOrderById(UUID customerId, UUID orderId);

    void pickupOrder(UUID customerId, UUID orderId);

}
