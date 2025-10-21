package com.example.demo.controller;

import com.example.demo.domain.AddressService;
import com.example.demo.model.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping("/fetchAll")
    public CompletableFuture<Map<String, Object>> fetchAllData() {
        return addressService.fetchAllAsync();
    }
}
