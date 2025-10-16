package com.example.demo.domain;

import com.example.demo.model.Address;
import com.example.demo.repo.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public Address fetchAndSaveAddress() {

        String url = "https://fakerapi.it/api/v2/addresses?_quantity=1";
        log.info("Fetching address from Faker API: {}", url);

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response == null || !response.containsKey("data")) {
            throw new RuntimeException("Invalid API response");
        }

        List<Map<String, Object>> dataList = (List<Map<String, Object>>) response.get("data");
        if (dataList.isEmpty()) {
            throw new RuntimeException("No address data recieved");
        }

        Map<String, Object> addrMap = dataList.get(0);

        Address address = Address.builder()
                .street((String) addrMap.get("street"))
                .streetName((String) addrMap.get("streetName"))
                .buildingNumber((String) addrMap.get("buildingNumber"))
                .city((String) addrMap.get("city"))
                .zipcode((String) addrMap.get("zipcode"))
                .country((String) addrMap.get("country"))
                .build();

        Address saved = addressRepository.save(address);
        log.info("Saved address to MongoDB: {}", saved);
        return saved;
    }
}
