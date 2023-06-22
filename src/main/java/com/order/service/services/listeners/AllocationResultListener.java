package com.order.service.services.listeners;

import com.ms.soda.events.AllocateOrderResult;
import com.order.service.config.JMSConfig;
import com.order.service.services.SodaOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocationResultListener {

    private final SodaOrderManager sodaOrderManager;

    @JmsListener(destination = JMSConfig.ALLOCATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult result) {
        if (result.getAllocationError()) {
            sodaOrderManager.sodaOrderAllocationFailed(result.getSodaOrderDto());
        } else {
            if (!result.getPendingInventory()) {
                sodaOrderManager.sodaOrderAllocationPassed(result.getSodaOrderDto());
            } else {
                sodaOrderManager.sodaOrderPendingInventory(result.getSodaOrderDto());
            }
        }
    }

}
