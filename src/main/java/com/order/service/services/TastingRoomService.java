package com.order.service.services;

import com.order.service.bootstrap.SodaOrderBootStrap;
import com.order.service.domain.Customer;
import com.order.service.repositories.CustomerRepository;
import com.order.service.web.model.SodaOrderDto;
import com.order.service.web.model.SodaOrderLineDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
public class TastingRoomService {

    private final SodaOrderService sodaOrderService;
    private final CustomerRepository customerRepository;

    private final List<String> sodaUpcs = new ArrayList<>(3);

    public TastingRoomService(SodaOrderService sodaOrderService, CustomerRepository customerRepository) {
        this.sodaOrderService = sodaOrderService;
        this.customerRepository = customerRepository;

        sodaUpcs.add(SodaOrderBootStrap.SODA_1_UPC);
        sodaUpcs.add(SodaOrderBootStrap.SODA_2_UPC);
        sodaUpcs.add(SodaOrderBootStrap.SODA_3_UPC);
    }


    @Transactional
    @Scheduled(fixedRate = 4000)
    public void placeTastingRoomOrder(){
        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(SodaOrderBootStrap.TASTING_ROOM);
        if (customerList.size() == 1){ //should be just one
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few tasting room customers found");
        }
    }

    private void doPlaceOrder(Customer customer) {
        String sodaToOrder = getRandomSodaUpc();

        SodaOrderLineDto sodaOrderLine = SodaOrderLineDto.builder()
                .upc(sodaToOrder)
                .orderQuantity(new Random().nextInt(6)) //todo externalize value to property
                .build();

        List<SodaOrderLineDto> sodaOrderLineSet = new ArrayList<>();
        sodaOrderLineSet.add(sodaOrderLine);

        SodaOrderDto sodaOrder = SodaOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .sodaOrderLines(sodaOrderLineSet)
                .build();

        SodaOrderDto savedOrder = sodaOrderService.placeOrder(customer.getId(), sodaOrder);
    }

    private String getRandomSodaUpc() {
        return sodaUpcs.get(new Random().nextInt(sodaUpcs.size()));
    }
}
