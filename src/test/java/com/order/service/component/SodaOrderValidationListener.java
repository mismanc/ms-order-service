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
    public void list(Message msg){
        ValidateOrderRequest request = (ValidateOrderRequest) msg.getPayload();
        System.out.println("############ SodaOrderValidationListener ############");
        jmsTemplate.convertAndSend(JMSConfig.VALIDATE_ORDER_RESPONSE_QUEUE, ValidateOrderResult.builder()
                .isValid(true).id(request.getSodaOrderDto().getId())
                .build());
    }

}
