package com.order.service.web.mappers;

import com.ms.soda.model.SodaOrderDto;
import com.order.service.domain.SodaOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateMapper.class, SodaOrderLineMapper.class})
public interface SodaOrderMapper {

    @Mapping(target="customerId", source = "customer.id")
    SodaOrderDto sodaOrderToDto(SodaOrder sodaOrder);

    SodaOrder dtoToSodaOrder(SodaOrderDto dto);

}
