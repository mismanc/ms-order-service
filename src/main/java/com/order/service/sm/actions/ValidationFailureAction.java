package com.order.service.sm.actions;

import com.order.service.domain.SodaOrderEventEnum;
import com.order.service.domain.SodaOrderStatusEnum;
import com.order.service.services.SodaOrderManagerImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidationFailureAction implements Action<SodaOrderStatusEnum, SodaOrderEventEnum> {
    @Override
    public void execute(StateContext<SodaOrderStatusEnum, SodaOrderEventEnum> stateContext) {
        String orderId = (String) stateContext.getMessage().getHeaders().get(SodaOrderManagerImpl.ORDER_ID_HEADER);
        log.error("Compensating Transaction :  Validation failed : " + orderId);
    }
}
