package com.order.service.services;

import com.order.service.bootstrap.BeerOrderBootStrap;
import com.order.service.domain.Customer;
import com.order.service.repositories.CustomerRepository;
import com.order.service.web.model.BeerOrderDto;
import com.order.service.web.model.BeerOrderLineDto;
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

    private final BeerOrderService beerOrderService;
    private final CustomerRepository customerRepository;

    private final List<String> beerUpcs = new ArrayList<>(3);

    public TastingRoomService(BeerOrderService beerOrderService, CustomerRepository customerRepository) {
        this.beerOrderService = beerOrderService;
        this.customerRepository = customerRepository;

        beerUpcs.add(BeerOrderBootStrap.BEER_1_UPC);
        beerUpcs.add(BeerOrderBootStrap.BEER_2_UPC);
        beerUpcs.add(BeerOrderBootStrap.BEER_3_UPC);
    }


    @Transactional
    @Scheduled(fixedRate = 4000)
    public void placeTastingRoomOrder(){
        List<Customer> customerList = customerRepository.findAllByCustomerNameLike(BeerOrderBootStrap.TASTING_ROOM);
        if (customerList.size() == 1){ //should be just one
            doPlaceOrder(customerList.get(0));
        } else {
            log.error("Too many or too few tasting room customers found");
        }
    }

    private void doPlaceOrder(Customer customer) {
        String beerToOrder = getRandomBeerUpc();

        BeerOrderLineDto beerOrderLine = BeerOrderLineDto.builder()
                .upc(beerToOrder)
                .orderQuantity(new Random().nextInt(6)) //todo externalize value to property
                .build();

        List<BeerOrderLineDto> beerOrderLineSet = new ArrayList<>();
        beerOrderLineSet.add(beerOrderLine);

        BeerOrderDto beerOrder = BeerOrderDto.builder()
                .customerId(customer.getId())
                .customerRef(UUID.randomUUID().toString())
                .beerOrderLines(beerOrderLineSet)
                .build();

        BeerOrderDto savedOrder = beerOrderService.placeOrder(customer.getId(), beerOrder);
    }

    private String getRandomBeerUpc() {
        return beerUpcs.get(new Random().nextInt(beerUpcs.size()));
    }
}
