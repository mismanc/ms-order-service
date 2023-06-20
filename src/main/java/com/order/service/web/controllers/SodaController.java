package com.order.service.web.controllers;

import com.ms.soda.model.SodaOrderDto;
import com.order.service.services.SodaOrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers/{customerId}")
public class SodaController {

    private final SodaOrderService sodaOrderService;


    public SodaController(SodaOrderService sodaOrderService) {
        this.sodaOrderService = sodaOrderService;
    }

    @GetMapping("/orders")
    public Page<SodaOrderDto> listOrders(@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
                                         @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,
                                         @PathVariable UUID customerId) {

        return sodaOrderService.listOrders(customerId, pageable);
    }

    @GetMapping("/orders/{orderId}")
    public SodaOrderDto getOrder(@PathVariable UUID customerId, @PathVariable UUID orderId){
        return sodaOrderService.getOrderById(customerId, orderId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SodaOrderDto placeOrder(@PathVariable UUID customerId, @RequestBody SodaOrderDto sodaOrderDto){
        return sodaOrderService.placeOrder(customerId, sodaOrderDto);
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable UUID customerId, @PathVariable UUID orderId){
        sodaOrderService.pickupOrder(customerId, orderId);
    }
}
