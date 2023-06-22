package com.order.service.sm;

import com.order.service.domain.SodaOrderEventEnum;
import com.order.service.domain.SodaOrderStatusEnum;
import com.order.service.sm.actions.AllocateOrderAction;
import com.order.service.sm.actions.ValidateOrderAction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class SodaOrderStateMachineConfig extends StateMachineConfigurerAdapter<SodaOrderStatusEnum, SodaOrderEventEnum> {

    private final ValidateOrderAction validateOrderAction;
    private final AllocateOrderAction allocateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<SodaOrderStatusEnum, SodaOrderEventEnum> states) throws Exception {
        states.withStates().initial(SodaOrderStatusEnum.NEW)
                .states(EnumSet.allOf(SodaOrderStatusEnum.class))
                .end(SodaOrderStatusEnum.PICKED_UP)
                .end(SodaOrderStatusEnum.DELIVERED)
                .end(SodaOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(SodaOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(SodaOrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<SodaOrderStatusEnum, SodaOrderEventEnum> transitions) throws Exception {
        transitions.withExternal()
                .source(SodaOrderStatusEnum.NEW).target(SodaOrderStatusEnum.VALIDATION_PENDING)
                .event(SodaOrderEventEnum.VALIDATE_ORDER).action(validateOrderAction)
                .and().withExternal()
                .source(SodaOrderStatusEnum.NEW).target(SodaOrderStatusEnum.VALIDATED).event(SodaOrderEventEnum.VALIDATION_PASSED)
                .and().withExternal()
                .source(SodaOrderStatusEnum.NEW).target(SodaOrderStatusEnum.VALIDATION_EXCEPTION).event(SodaOrderEventEnum.VALIDATION_FAILED)
                .and().withExternal()
                .source(SodaOrderStatusEnum.VALIDATED).target(SodaOrderStatusEnum.ALLOCATION_PENDING)
                .event(SodaOrderEventEnum.ALLOCATE_ORDER).action(allocateOrderAction)
        ;
    }
}
