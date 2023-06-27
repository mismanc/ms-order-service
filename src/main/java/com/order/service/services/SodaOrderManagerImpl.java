package com.order.service.services;

import com.ms.soda.model.SodaOrderDto;
import com.order.service.domain.SodaOrder;
import com.order.service.domain.SodaOrderEventEnum;
import com.order.service.domain.SodaOrderStatusEnum;
import com.order.service.repositories.SodaOrderRepository;
import com.order.service.sm.SodaSMInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
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
        SodaOrder savedSoadOrder = saveSodaOrder(sodaOrder);
        sendSodaOrderEvent(savedSoadOrder, SodaOrderEventEnum.VALIDATE_ORDER);
        return savedSoadOrder;
    }

    @Transactional
    @Override
    public SodaOrder saveSodaOrder(SodaOrder sodaOrder) {
        sodaOrder.setId(null);
        sodaOrder.setOrderStatus(SodaOrderStatusEnum.NEW);
        return sodaOrderRepository.saveAndFlush(sodaOrder);
    }

    @Transactional
    @Override
    public void processValidationResult(UUID id, Boolean isValid) {
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(id);
        if (sodaOrderOptional.isPresent()) {
            SodaOrder sodaOrder = sodaOrderOptional.get();
            if (isValid) {
                sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.VALIDATION_PASSED);
                Optional<SodaOrder> validated = sodaOrderRepository.findById(id);
                if (validated.isPresent())
                    sendSodaOrderEvent(validated.get(), SodaOrderEventEnum.ALLOCATE_ORDER);
                else log.error("Order Not Found ID : " + id);
            } else {
                sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.VALIDATION_FAILED);
            }
        } else log.error("Order Not Found ID : " + id);
    }

    @Override
    public void sodaOrderAllocationPassed(SodaOrderDto sodaOrderDto) {
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(sodaOrderDto.getId());
        sodaOrderOptional.ifPresentOrElse(sodaOrder -> {
            sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.ALLOCATION_SUCCESS);
            updateAllocatedQty(sodaOrderDto);
        }, () -> log.error("Soda order not found: " + sodaOrderDto.getId()));
    }

    @Override
    public void pickUpOrder(UUID id) {
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(id);
        sodaOrderOptional.ifPresentOrElse(sodaOrder -> {
           sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.SODA_ORDER_PICKED_UP);
        }, () -> log.error("Soda order not found: " + id));
    }

    @Override
    public void sodaOrderPendingInventory(SodaOrderDto sodaOrderDto) {
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(sodaOrderDto.getId());
        sodaOrderOptional.ifPresentOrElse(sodaOrder -> {
            sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.ALLOCATION_NO_INVENTORY);
            updateAllocatedQty(sodaOrderDto);
        }, () -> log.error("Soda order not found: " + sodaOrderDto.getId()));
    }

    public void updateAllocatedQty(SodaOrderDto sodaOrderDto) {
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(sodaOrderDto.getId());
        sodaOrderOptional.ifPresentOrElse(allocatedOrder -> {
            allocatedOrder.getSodaOrderLines().forEach(orderLine -> {
                sodaOrderDto.getSodaOrderLines().forEach(orderLineDto -> {
                    if (orderLine.getId().equals(orderLineDto.getId())) {
                        orderLine.setQuantityAllocated(orderLineDto.getQuantityAllocated());
                    }
                });
            });
            sodaOrderRepository.saveAndFlush(allocatedOrder);
        }, () -> log.error("Soda order not found: " + sodaOrderDto.getId()));
    }

    @Override
    public void sodaOrderAllocationFailed(SodaOrderDto sodaOrderDto) {
        Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(sodaOrderDto.getId());
        sodaOrderOptional.ifPresentOrElse(sodaOrder -> {
            sendSodaOrderEvent(sodaOrder, SodaOrderEventEnum.ALLOCATION_FAILED);
        }, () -> log.error("Soda order not found: " + sodaOrderDto.getId()));
    }

    @Override
    public void sendSodaOrderEvent(SodaOrder sodaOrder, SodaOrderEventEnum sodaOrderEventEnum) {
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
