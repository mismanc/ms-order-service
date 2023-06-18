package com.order.service.services;

import com.order.service.domain.Customer;
import com.order.service.domain.SodaOrder;
import com.order.service.domain.SodaOrderStatusEnum;
import com.order.service.repositories.CustomerRepository;
import com.order.service.repositories.SodaOrderRepository;
import com.order.service.web.mappers.SodaOrderMapper;
import com.order.service.web.model.SodaOrderDto;
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
public class SodaOrderServiceImpl implements SodaOrderService {

    private final SodaOrderRepository sodaOrderRepository;

    private final CustomerRepository customerRepository;

    private final SodaOrderMapper sodaOrderMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    public SodaOrderServiceImpl(SodaOrderRepository sodaOrderRepository, CustomerRepository customerRepository, SodaOrderMapper sodaOrderMapper, ApplicationEventPublisher applicationEventPublisher) {
        this.sodaOrderRepository = sodaOrderRepository;
        this.customerRepository = customerRepository;
        this.sodaOrderMapper = sodaOrderMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public Page<SodaOrderDto> listOrders(UUID customerId, Pageable pageable) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            Page<SodaOrder> sodaOrdersPage = sodaOrderRepository.findAllByCustomer_Id(customerId, pageable);
            return new PageImpl<>(sodaOrdersPage.stream().map(sodaOrderMapper::sodaOrderToDto).collect(Collectors.toList()),
                    pageable, sodaOrdersPage.getTotalElements());
        }
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    @Transactional
    public SodaOrderDto placeOrder(UUID customerId, SodaOrderDto sodaOrderDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            SodaOrder sodaOrder = sodaOrderMapper.dtoToSodaOrder(sodaOrderDto);
            sodaOrder.setId(null);
            sodaOrder.setCustomer(customerOptional.get());
            sodaOrder.setOrderStatus(SodaOrderStatusEnum.NEW);
            sodaOrder.getSodaOrderLines().forEach(line -> line.setSodaOrder(sodaOrder));
            SodaOrder saved = sodaOrderRepository.save(sodaOrder);
            log.debug("Save Soda Order: " + saved.getId().toString());
            return sodaOrderMapper.sodaOrderToDto(saved);
        }
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public SodaOrderDto getOrderById(UUID customerId, UUID orderId) {
        return sodaOrderMapper.sodaOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        SodaOrder sodaOrder = getOrder(customerId, orderId);
        sodaOrder.setOrderStatus(SodaOrderStatusEnum.PICKED_UP);

        sodaOrderRepository.save(sodaOrder);
    }

    private SodaOrder getOrder(UUID customerId, UUID orderId) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        if (customerOptional.isPresent()) {
            return sodaOrderRepository.findByIdAndCustomer_Id(orderId, customerId).orElseThrow(() -> new RuntimeException("Soda Order Not Found"));
        }
        throw new RuntimeException("Customer Not Found");
    }
}
