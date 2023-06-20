package com.order.service.web.mappers;

import com.ms.soda.model.SodaDto;
import com.ms.soda.model.SodaOrderLineDto;
import com.order.service.domain.SodaOrderLine;
import com.order.service.services.soda.SodaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Optional;

public abstract class SodaOrderLineDecorator implements SodaOrderLineMapper {

    private SodaService sodaService;
    private SodaOrderLineMapper sodaOrderLineMapper;

    @Autowired
    public void setSodaService(SodaService sodaService) {
        this.sodaService = sodaService;
    }

    @Autowired
    @Qualifier("delegate")
    public void setSodaOrderLineMapper(SodaOrderLineMapper sodaOrderLineMapper) {
        this.sodaOrderLineMapper = sodaOrderLineMapper;
    }

    @Override
    public SodaOrderLineDto sodaOrderLineToDto(SodaOrderLine line) {
        SodaOrderLineDto sodaOrderLineDto = sodaOrderLineMapper.sodaOrderLineToDto(line);
        Optional<SodaDto> sodaDtoOptional = sodaService.getSodaByUpc(line.getUpc());
        sodaDtoOptional.ifPresent(sodaDto -> {
            sodaOrderLineDto.setSodaName(sodaDto.getSodaName());
            sodaOrderLineDto.setSodaStyle(sodaDto.getSodaStyle());
            sodaOrderLineDto.setPrice(sodaDto.getPrice());
            sodaOrderLineDto.setSodaId(sodaDto.getId());
        });
        return sodaOrderLineDto;
    }
}
