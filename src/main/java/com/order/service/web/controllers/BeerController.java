package com.order.service.web.controllers;

import com.order.service.services.BeerOrderService;
import com.order.service.web.model.BeerOrderDto;
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
public class BeerController {

    private final BeerOrderService beerOrderService;


    public BeerController(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @GetMapping("/orders")
    public Page<BeerOrderDto> listOrders(@PageableDefault(sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
                                         @RequestParam MultiValueMap<String, String> queryParams, UriComponentsBuilder uriBuilder,
                                         @PathVariable UUID customerId) {

        return beerOrderService.listOrders(customerId, pageable);
    }

    @GetMapping("/orders/{orderId}")
    public BeerOrderDto getOrder(@PathVariable UUID customerId, @PathVariable UUID orderId){
        return beerOrderService.getOrderById(customerId, orderId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeerOrderDto placeOrder(@PathVariable UUID customerId, @RequestBody BeerOrderDto beerOrderDto){
        return beerOrderService.placeOrder(customerId, beerOrderDto);
    }

    @PutMapping("/orders/{orderId}/pickup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void pickupOrder(@PathVariable UUID customerId, @PathVariable UUID orderId){
        beerOrderService.pickupOrder(customerId, orderId);
    }
}
