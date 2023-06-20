package com.order.service.services.soda;

import com.ms.model.SodaDto;

import java.util.Optional;
import java.util.UUID;

public interface SodaService {

    Optional<SodaDto> getSodaById(UUID id);

    Optional<SodaDto> getSodaByUpc(String upc);

}
