package com.order.service.services;

import com.order.service.domain.BeerOrder;
import com.order.service.domain.Customer;
import com.order.service.domain.OrderStatusEnum;
import com.order.service.repositories.BeerOrderRepository;
import com.order.service.repositories.CustomerRepository;
import com.order.service.web.mappers.BeerOrderMapper;
import com.order.service.web.model.BeerOrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;

    private final CustomerRepository customerRepository;

    private final BeerOrderMapper beerOrderMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    public BeerOrderServiceImpl(BeerOrderRepository beerOrderRepository, CustomerRepository customerRepository, BeerOrderMapper beerOrderMapper, ApplicationEventPublisher applicationEventPublisher) {
        this.beerOrderRepository = beerOrderRepository;
        this.customerRepository = customerRepository;
        this.beerOrderMapper = beerOrderMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Page<BeerOrderDto> listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            Page<BeerOrder> beerOrdersPage = beerOrderRepository.findAllByCustomer_Id(customerId, pageable);
            return new PageImpl<>(beerOrdersPage.stream().map(beerOrderMapper::beerOrderToDto).collect(Collectors.toList()),
                    pageable, beerOrdersPage.getTotalElements());
        }
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    @Transactional
    public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            BeerOrder beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
            beerOrder.setId(null);
            beerOrder.setCustomer(customerOptional.get());
            beerOrder.setOrderStatus(OrderStatusEnum.NEW);
            beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));
            BeerOrder saved = beerOrderRepository.save(beerOrder);
            log.debug("Save Beer Order: " + saved.getId().toString());
            return beerOrderMapper.beerOrderToDto(saved);
        }
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
        return beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        BeerOrder beerOrder = getOrder(customerId, orderId);
        beerOrder.setOrderStatus(OrderStatusEnum.PICKED_UP);

        beerOrderRepository.save(beerOrder);
    }

    private BeerOrder getOrder(UUID customerId, UUID orderId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            return beerOrderRepository.findByIdAndCustomer_Id(orderId, customerId).orElseThrow(() -> new RuntimeException("Beer Order Not Found"));
        }
        throw new RuntimeException("Customer Not Found");
    }
}
