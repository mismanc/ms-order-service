package com.order.service.web.mappers;

import com.order.service.domain.SodaOrderLine;
import com.order.service.web.model.SodaOrderLineDto;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(SodaOrderLineDecorator.class)
public interface SodaOrderLineMapper {

    SodaOrderLineDto sodaOrderLineToDto(SodaOrderLine line);

    SodaOrderLine dtoToSodaOrderLine(SodaOrderLineDto dto);

}
