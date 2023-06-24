package com.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.ms.soda.model.SodaDto;
import com.order.service.domain.Customer;
import com.order.service.domain.SodaOrder;
import com.order.service.domain.SodaOrderLine;
import com.order.service.domain.SodaOrderStatusEnum;
import com.order.service.repositories.CustomerRepository;
import com.order.service.repositories.SodaOrderRepository;
import com.order.service.services.SodaOrderManager;
import com.order.service.services.soda.SodaServiceImpl;
import com.order.service.wiremock.WireMockInitializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ContextConfiguration(initializers = {WireMockInitializer.class})
@SpringBootTest
public class SodaOrderManagerIT {

    @Autowired
    SodaOrderManager sodaOrderManager;

    @Autowired
    SodaOrderRepository sodaOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    WireMockServer wireMockServer;

    @Autowired
    ObjectMapper objectMapper;

    Customer testCustomer;

    UUID sodaId = UUID.randomUUID();


    @BeforeEach
    void setup() {
        testCustomer = customerRepository.save(Customer.builder().customerName("Test customer").build());
    }

    @Test
    void newToAllocated() throws JsonProcessingException {
        SodaDto sodaDto = SodaDto.builder().id(sodaId).upc("123454").build();

        wireMockServer.stubFor(WireMock.get(SodaServiceImpl.SODA_UPC_PATH_V1 + sodaDto.getUpc()).willReturn(
                okJson(objectMapper.writeValueAsString(new PageImpl<>(Collections.singletonList(sodaDto))))
        ));

        SodaOrder sodaOrder = createSodaOrder();
        SodaOrder savedSodaOrder = sodaOrderManager.newSodaOrder(sodaOrder);
        assertNotNull(savedSodaOrder);
        assertEquals(SodaOrderStatusEnum.ALLOCATED, savedSodaOrder.getOrderStatus());
    }

    public SodaOrder createSodaOrder() {
        SodaOrder sodaOrder = SodaOrder.builder().customer(testCustomer).build();
        Set<SodaOrderLine> lines = new HashSet<>();
        lines.add(SodaOrderLine.builder().sodaId(sodaId).upc("123454")
                .orderQuantity(1).sodaOrder(sodaOrder).build());
        sodaOrder.setSodaOrderLines(lines);
        return sodaOrder;
    }
}
