package com.order.service.services;

import com.ms.soda.model.SodaOrderDto;
import com.order.service.domain.SodaOrder;
import com.order.service.domain.SodaOrderEventEnum;
import com.order.service.domain.SodaOrderStatusEnum;
import com.order.service.repositories.SodaOrderRepository;
import com.order.service.sm.SodaSMInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SodaOrderManagerImpl implements SodaOrderManager {

    public static final String ORDER_ID_HEADER = "ORDER_ID";

    private final StateMachineFactory<SodaOrderStatusEnum, SodaOrderEventEnum> stateMachineFactory;
    private final SodaOrderRepository sodaOrderRepository;
    private final SodaSMInterceptor smInterceptor;

    @Transactional
    @Override
    public SodaOrder newSodaOrder(SodaOrder sodaOrder) {
        sodaOrder.setId(null);
        sodaOrder.setOrderStatus(SodaOrderStatusEnum.NEW);
        SodaOrder savedSoadOrder = sodaOrderRepository.save(sodaOrder);
        sendSodaOrderEvent(savedSoadOrder, SodaOrderEventEnum.VALIDATE_ORDER);
        return savedSoadOrder;
    }

    @Override
    public void processValidationResult(UUID id, Boolean isValid) {
        SodaOrder sodaOrder = sodaOrderRepository.findOneById(id);
        if (isValid) {
            sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.VALIDATION_PASSED);
            SodaOrder validated = sodaOrderRepository.findOneById(id);
            sendSodaOrderEvent(validated, SodaOrderEventEnum.ALLOCATE_ORDER);
        } else {
            sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.VALIDATION_FAILED);
        }
    }

    @Override
    public void sodaOrderAllocationPassed(SodaOrderDto sodaOrderDto) {
        SodaOrder sodaOrder = sodaOrderRepository.findOneById(sodaOrderDto.getId());
        sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.ALLOCATION_SUCCESS);
        updateAllocatedQty(sodaOrderDto);
    }

    @Override
    public void sodaOrderPendingInventory(SodaOrderDto sodaOrderDto) {
        SodaOrder sodaOrder = sodaOrderRepository.findOneById(sodaOrderDto.getId());
        sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.ALLOCATION_NO_INVENTORY);
        updateAllocatedQty(sodaOrderDto);
    }

    private void updateAllocatedQty(SodaOrderDto sodaOrderDto) {
        SodaOrder allocatedOrder = sodaOrderRepository.findOneById(sodaOrderDto.getId());
        allocatedOrder.getSodaOrderLines().forEach(orderLine -> {
            sodaOrderDto.getSodaOrderLines().forEach(orderLineDto -> {
                if (orderLine.getId().equals(orderLineDto.getId())) {
                    orderLine.setQuantityAllocated(orderLineDto.getQuantityAllocated());
                }
            });
        });
        sodaOrderRepository.saveAndFlush(allocatedOrder);
    }

    @Override
    public void sodaOrderAllocationFailed(SodaOrderDto sodaOrderDto) {
        SodaOrder sodaOrder = sodaOrderRepository.findOneById(sodaOrderDto.getId());
        sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.ALLOCATION_FAILED);
    }

    private void sendSodaOrderEvent(SodaOrder sodaOrder, SodaOrderEventEnum sodaOrderEventEnum) {
        StateMachine<SodaOrderStatusEnum, SodaOrderEventEnum> sm = build(sodaOrder);
        Message<SodaOrderEventEnum> message = MessageBuilder.withPayload(sodaOrderEventEnum)
                .setHeader(ORDER_ID_HEADER, sodaOrder.getId().toString())
                .build();
        sm.sendEvent(message);
    }

    private StateMachine<SodaOrderStatusEnum, SodaOrderEventEnum> build(SodaOrder sodaOrder) {
        StateMachine<SodaOrderStatusEnum, SodaOrderEventEnum> sm = stateMachineFactory.getStateMachine(sodaOrder.getId());
        sm.stop();
        sm.getStateMachineAccessor().doWithAllRegions(sma -> {
            sma.addStateMachineInterceptor(smInterceptor);
            sma.resetStateMachine(new DefaultStateMachineContext<>(sodaOrder.getOrderStatus(), null, null, null));
        });
        sm.start();
        return sm;
    }

}
