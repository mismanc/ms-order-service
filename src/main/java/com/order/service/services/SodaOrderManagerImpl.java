package com.order.service.services;

import com.order.service.domain.SodaOrder;
import com.order.service.domain.SodaOrderEventEnum;
import com.order.service.domain.SodaOrderStatusEnum;
import com.order.service.repositories.SodaOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SodaOrderManagerImpl implements SodaOrderManager {

    private final StateMachineFactory<SodaOrderStatusEnum, SodaOrderEventEnum> stateMachineFactory;
    private final SodaOrderRepository sodaOrderRepository;

    @Transactional
    @Override
    public SodaOrder newSodaOrder(SodaOrder sodaOrder) {
        sodaOrder.setId(null);
        sodaOrder.setOrderStatus(SodaOrderStatusEnum.NEW);
        SodaOrder savedSoadOrder = sodaOrderRepository.save(sodaOrder);
        sendSodaOrderEvent(savedSoadOrder, SodaOrderEventEnum.VALIDATE_ORDER);
        return savedSoadOrder;
    }

    private void sendSodaOrderEvent(SodaOrder sodaOrder, SodaOrderEventEnum sodaOrderEventEnum){
        StateMachine<SodaOrderStatusEnum, SodaOrderEventEnum> sm = build(sodaOrder);
        Message<SodaOrderEventEnum> message = MessageBuilder.withPayload(sodaOrderEventEnum).build();
        sm.sendEvent(message);
    }

    private StateMachine<SodaOrderStatusEnum, SodaOrderEventEnum> build(SodaOrder sodaOrder){
        StateMachine<SodaOrderStatusEnum, SodaOrderEventEnum> sm = stateMachineFactory.getStateMachine(sodaOrder.getId());
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(sma-> sma.resetStateMachine(new DefaultStateMachineContext<>(sodaOrder.getOrderStatus(), null, null, null)));
        sm.start();
        return sm;
    }

}
