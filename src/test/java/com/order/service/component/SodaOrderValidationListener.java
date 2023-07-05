package com.order.service.component;

import com.ms.soda.events.ValidateOrderRequest;
import com.ms.soda.events.ValidateOrderResult;
import com.order.service.config.JMSConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class SodaOrderValidationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JMSConfig.VALIDATE_ORDER_QUEUE)
    public void list(Message msg) {
        ValidateOrderRequest request = (ValidateOrderRequest) msg.getPayload();
        boolean valid = true;
        boolean sendResponse = true;
        if (request.getSodaOrderDto().getCustomerRef() != null && request.getSodaOrderDto().getCustomerRef().equals("fail-validation")) {
            valid = false;
        } else if (request.getSodaOrderDto().getCustomerRef() != null && request.getSodaOrderDto().getCustomerRef().equals("dont-validate")) {
            sendResponse = false;
        }
        System.out.println("############ SodaOrderValidationListener ############");
        if (sendResponse) {
            jmsTemplate.convertAndSend(JMSConfig.VALIDATE_ORDER_RESPONSE_QUEUE, ValidateOrderResult.builder()
                    .isValid(valid).id(request.getSodaOrderDto().getId())
                    .build());
        }
    }

}
