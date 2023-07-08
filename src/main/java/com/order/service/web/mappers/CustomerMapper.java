package com.order.service.web.mappers;

import com.ms.soda.model.CustomerDto;
import com.order.service.domain.Customer;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface CustomerMapper {

    CustomerDto customerToDto(Customer customer);

    Customer dtoToSodaOrder(CustomerDto dto);

}
