package com.example.demo.adapter;

import com.example.demo.domain.AddressService;
import com.example.demo.model.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping("/fetch-and-save")
    public Address fetchAndSaveAddress() {
        return addressService.fetchAndSaveAddress();
    }
}
