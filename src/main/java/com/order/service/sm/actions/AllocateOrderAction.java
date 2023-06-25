package com.order.service.sm.actions;

import com.ms.soda.events.AllocateOrderRequest;
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

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AllocateOrderAction implements Action<SodaOrderStatusEnum, SodaOrderEventEnum> {

    private final SodaOrderRepository sodaOrderRepository;
    private final JmsTemplate jmsTemplate;
    private final SodaOrderMapper sodaOrderMapper;

    @Override
    public void execute(StateContext<SodaOrderStatusEnum, SodaOrderEventEnum> stateContext) {
        String orderId = (String) stateContext.getMessage().getHeaders().get(SodaOrderManagerImpl.ORDER_ID_HEADER);
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(UUID.fromString(orderId));

        sodaOrderOptional.ifPresentOrElse(sodaOrder -> {
            jmsTemplate.convertAndSend(JMSConfig.ALLOCATE_ORDER_QUEUE, AllocateOrderRequest.builder()
                    .sodaOrderDto(sodaOrderMapper.sodaOrderToDto(sodaOrder)).build());

            log.debug("Sent validation request to queue for order id : " + orderId);
        }, () -> log.error("Soda Order Not Found " + orderId));

    }
}
