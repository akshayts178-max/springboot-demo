package com.example.demo.controller;

import com.example.demo.domain.AddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping("/fetchAll")
    public CompletableFuture<Map<String, Object>> fetchAllData() {
        log.info("Received request to fetch all async address data");
        return addressService.fetchAllAsync();
    }
}
