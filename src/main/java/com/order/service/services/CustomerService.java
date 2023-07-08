package com.order.service.services;

import com.ms.soda.model.CustomerDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {
    Page<CustomerDto> listCustomers(Pageable pageable);
}
