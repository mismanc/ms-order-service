package com.order.service.sm;

import com.order.service.domain.SodaOrder;
import com.order.service.domain.SodaOrderEventEnum;
import com.order.service.domain.SodaOrderStatusEnum;
import com.order.service.repositories.SodaOrderRepository;
import com.order.service.services.SodaOrderManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class SodaSMInterceptor extends StateMachineInterceptorAdapter<SodaOrderStatusEnum, SodaOrderEventEnum> {

    private final SodaOrderRepository sodaOrderRepository;

    public SodaSMInterceptor(SodaOrderRepository sodaOrderRepository) {
        this.sodaOrderRepository = sodaOrderRepository;
    }

    @Transactional
    @Override
    public void preStateChange(State<SodaOrderStatusEnum, SodaOrderEventEnum> state, Message<SodaOrderEventEnum> message, Transition<SodaOrderStatusEnum, SodaOrderEventEnum> transition, StateMachine<SodaOrderStatusEnum, SodaOrderEventEnum> stateMachine, StateMachine<SodaOrderStatusEnum, SodaOrderEventEnum> rootStateMachine) {
        Optional.ofNullable(message).flatMap(msg ->
                        Optional.ofNullable((String) msg.getHeaders().getOrDefault(SodaOrderManagerImpl.ORDER_ID_HEADER, "-1")))
                .ifPresent(orderId -> {
                    log.debug("Saving state for order id: " + orderId + " Status: " + state.getId());

                    Optional<SodaOrder> sodaOrderOptional = sodaOrderRepository.findById(UUID.fromString(orderId));
                    if (sodaOrderOptional.isPresent()) {
                        sodaOrderOptional.get().setOrderStatus(state.getId());
                        sodaOrderRepository.saveAndFlush(sodaOrderOptional.get());
                    }
                });
    }
}
