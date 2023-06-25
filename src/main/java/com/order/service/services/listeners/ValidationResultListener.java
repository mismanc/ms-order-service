package com.order.service.services.listeners;

import com.ms.soda.events.ValidateOrderResult;
import com.order.service.config.JMSConfig;
import com.order.service.services.SodaOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationResultListener {

    private final SodaOrderManager sodaOrderManager;

    @JmsListener(destination = JMSConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult validateOrderResult){
        UUID id = validateOrderResult.getId();
        log.debug("Validation for Order Id: " + id);
        sodaOrderManager.processValidationResult(id, validateOrderResult.getIsValid());
    }

}
