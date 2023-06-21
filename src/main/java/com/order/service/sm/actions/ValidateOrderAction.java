package com.order.service.sm.actions;

import com.ms.soda.events.ValidateOrderRequest;
import com.order.service.config.JMSConfig;
import com.order.service.domain.SodaOrder;
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
public class ValidateOrderAction implements Action<SodaOrderStatusEnum, SodaOrderEventEnum> {

    private final SodaOrderRepository sodaOrderRepository;
    private final JmsTemplate jmsTemplate;
    private final SodaOrderMapper sodaOrderMapper;

    @Override
    public void execute(StateContext<SodaOrderStatusEnum, SodaOrderEventEnum> stateContext) {
        String orderId = (String) stateContext.getMessage().getHeaders().get(SodaOrderManagerImpl.ORDER_ID_HEADER);
        SodaOrder sodaOrder = sodaOrderRepository.findOneById(UUID.fromString(orderId));

        jmsTemplate.convertAndSend(JMSConfig.VALIDATE_ORDER_QUEUE, ValidateOrderRequest.builder()
                .sodaOrderDto(sodaOrderMapper.sodaOrderToDto(sodaOrder)).build());

        log.debug("Sent validation request to queue for order id : " + orderId);
    }
}
