package com.order.service.services;

import com.ms.soda.model.CustomerDto;
import com.order.service.domain.Customer;
import com.order.service.repositories.CustomerRepository;
import com.order.service.web.mappers.CustomerMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Page<CustomerDto> listCustomers(Pageable pageable) {

        Page<Customer> customersPage = customerRepository.findAll(pageable);
        return new PageImpl<>(customersPage.stream().map(customerMapper::customerToDto).collect(Collectors.toList()),
                pageable, customersPage.getTotalElements());
    }
}
