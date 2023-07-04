package com.order.service.sm.actions;

import com.ms.soda.events.AllocationFailureEvent;
import com.order.service.config.JMSConfig;
import com.order.service.domain.SodaOrderEventEnum;
import com.order.service.domain.SodaOrderStatusEnum;
import com.order.service.repositories.SodaOrderRepository;
import com.order.service.services.SodaOrderManagerImpl;
import com.order.service.web.mappers.SodaOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocationFailureAction implements Action<SodaOrderStatusEnum, SodaOrderEventEnum> {

    private final SodaOrderRepository sodaOrderRepository;
    private final JmsTemplate jmsTemplate;
    private final SodaOrderMapper sodaOrderMapper;

    @Override
    public void execute(StateContext<SodaOrderStatusEnum, SodaOrderEventEnum> stateContext) {
        String orderId = (String) stateContext.getMessage().getHeaders().get(SodaOrderManagerImpl.ORDER_ID_HEADER);
        jmsTemplate.convertAndSend(JMSConfig.ALLOCATION_FAILURE_QUEUE, AllocationFailureEvent.builder()
                .orderId(UUID.fromString(orderId)).build());

        log.debug("Sent allocation failure message for order id : " + orderId);

    }
}
