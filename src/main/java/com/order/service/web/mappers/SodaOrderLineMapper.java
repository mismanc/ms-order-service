package com.order.service.web.mappers;

import com.ms.model.SodaOrderLineDto;
import com.order.service.domain.SodaOrderLine;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;

@Mapper(uses = {DateMapper.class})
@DecoratedWith(SodaOrderLineDecorator.class)
public interface SodaOrderLineMapper {

    SodaOrderLineDto sodaOrderLineToDto(SodaOrderLine line);

    SodaOrderLine dtoToSodaOrderLine(SodaOrderLineDto dto);

}
