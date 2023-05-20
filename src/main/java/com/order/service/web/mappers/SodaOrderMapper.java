package com.order.service.web.mappers;

import com.order.service.domain.SodaOrder;
import com.order.service.web.model.SodaOrderDto;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
public interface SodaOrderMapper {

    SodaOrderDto sodaOrderToDto(SodaOrder sodaOrder);

    SodaOrder dtoToSodaOrder(SodaOrderDto dto);

}
