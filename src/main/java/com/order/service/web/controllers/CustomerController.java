package com.order.service.web.controllers;

import com.ms.soda.model.CustomerDto;
import com.order.service.services.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/customers/")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public Page<CustomerDto> listCustomers(@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
                                        @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder) {

        return customerService.listCustomers(pageable);
    }
}
