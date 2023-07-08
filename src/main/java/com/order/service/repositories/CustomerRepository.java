package com.order.service.repositories;

import com.order.service.domain.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.UUID;

public interface CustomerRepository extends PagingAndSortingRepository<Customer, UUID> {

    List<Customer> findAllByCustomerNameLike(String customerName);

    long countByCustomerNameLike(String customerName);

}
