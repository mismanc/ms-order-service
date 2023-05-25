package com.order.service.services.soda;

import com.order.service.web.model.SodaDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@ConfigurationProperties(prefix = "soda.order", ignoreUnknownFields = false)
@Service
public class SodaServiceImpl implements SodaService {

    private final String SODA_PATH_V1 = "/api/v1/soda/";
    private final String SODA_UPC_PATH_V1 = "/api/v1/soda/upc/";
    private final RestTemplate restTemplate;

    private String serviceHost;

    public SodaServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Optional<SodaDto> getSodaById(UUID id) {
        return Optional.ofNullable(restTemplate.getForObject(serviceHost + SODA_PATH_V1 + id.toString(), SodaDto.class));
    }

    @Override
    public Optional<SodaDto> getSodaByUpc(String upc) {
        return Optional.ofNullable(restTemplate.getForObject(serviceHost + SODA_UPC_PATH_V1 + upc, SodaDto.class));
    }

    public void setServiceHost(String serviceHost) {
        this.serviceHost = serviceHost;
    }
}
