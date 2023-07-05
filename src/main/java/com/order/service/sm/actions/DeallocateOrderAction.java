package com.order.service.sm.actions;

import com.ms.soda.events.DeallocateOrderRequest;
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
public class DeallocateOrderAction implements Action<SodaOrderStatusEnum, SodaOrderEventEnum> {

    private final SodaOrderRepository sodaOrderRepository;
    private final JmsTemplate jmsTemplate;
    private final SodaOrderMapper sodaOrderMapper;

    @Override
    public void execute(StateContext<SodaOrderStatusEnum, SodaOrderEventEnum> stateContext) {
        String orderId = (String) stateContext.getMessage().getHeaders().get(SodaOrderManagerImpl.ORDER_ID_HEADER);
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(UUID.fromString(orderId));

        if (sodaOrderOptional.isPresent()) {
            jmsTemplate.convertAndSend(JMSConfig.DEALLOCATE_ORDER_QUEUE, DeallocateOrderRequest.builder()
                    .sodaOrderDto(sodaOrderMapper.sodaOrderToDto(sodaOrderOptional.get())).build());

            log.debug("Sent deallocate request to queue for order id : " + orderId);
        } else throw new RuntimeException("Soda Order Not Found " + orderId);

    }
}
