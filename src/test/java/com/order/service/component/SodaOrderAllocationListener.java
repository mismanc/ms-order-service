package com.order.service.component;

import com.ms.soda.events.AllocateOrderRequest;
import com.ms.soda.events.AllocateOrderResult;
import com.order.service.config.JMSConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SodaOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JMSConfig.ALLOCATE_ORDER_QUEUE)
    public void list(Message msg){
        AllocateOrderRequest request = (AllocateOrderRequest) msg.getPayload();
        request.getSodaOrderDto().getSodaOrderLines().forEach(line->{
            line.setQuantityAllocated(line.getOrderQuantity());
        });
        System.out.println("############ AllocationListener WORKED ############");
        jmsTemplate.convertAndSend(JMSConfig.ALLOCATE_ORDER_RESPONSE_QUEUE, AllocateOrderResult.builder()
                        .sodaOrderDto(request.getSodaOrderDto()).pendingInventory(false)
                        .allocationError(false).build());
    }

}
