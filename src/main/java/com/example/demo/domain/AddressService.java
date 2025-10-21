package com.example.demo.domain;

import com.example.demo.model.Address;
import com.example.demo.repo.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;
    private final RestTemplate restTemplate = new RestTemplate();

        private static final String FAKER_URL = "https://fakerapi.it/api/v2/addresses?_quantity=1";
        private static final String AGIFY_URL = "https://api.agify.io?name=meelad";
        private static final String GENDERIZE_URL = "https://api.genderize.io?name=luc";

        @Async
        public CompletableFuture<Address> fetchAddressAsync() {
            log.info("Fetching address from: {}", FAKER_URL);
        Map<String, Object> response = restTemplate.getForObject(FAKER_URL, Map.class);

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
        log.info("Saved address: {}", saved);
        return CompletableFuture.completedFuture(saved);
    }

    @Async
    public CompletableFuture<Map<String, Object>> fetchGenderizeAsync() {
        log.info("Fetching gender prediction from: {}", GENDERIZE_URL);
        Map<String, Object> response = restTemplate.getForObject(GENDERIZE_URL, Map.class);
        return CompletableFuture.completedFuture(response);
    }

    @Async
    public CompletableFuture<Map<String, Object>> fetchAgifyAsync() {
        log.info("Fetching age prediction from: {}", AGIFY_URL);
        Map<String, Object> response = restTemplate.getForObject(AGIFY_URL, Map.class);
        return CompletableFuture.completedFuture(response);
    }

    public CompletableFuture<Map<String, Object>> fetchAllAsync() {
        CompletableFuture<Address> addressFuture = fetchAddressAsync();
        CompletableFuture<Map<String, Object>> agifyFuture = fetchAgifyAsync();
        CompletableFuture<Map<String, Object>> genderizeFuture = fetchGenderizeAsync();

        return CompletableFuture.allOf(addressFuture, agifyFuture, genderizeFuture)
                .thenApply(voided -> {
                    try {
                        Address address = addressFuture.join();
                        Map<String, Object> ageMap = agifyFuture.join();
                        Map<String, Object> genderMap = genderizeFuture.join();
                        address.setPredictedAge((Integer) ageMap.get("age"));
                        address.setPredictedGender((String) genderMap.get("gender"));
                        address.setGenderProbability(
                                genderMap.get("probability") instanceof Number
                                        ? ((Number) genderMap.get("probability")).doubleValue()
                                        : null
                        );
                        Address updated = addressRepository.save(address);
                        Map<String, Object> result = new LinkedHashMap<>();
                        result.put("address", updated);
                        result.put("agePrediction", ageMap);
                        result.put("genderPrediction", genderMap);
                        return result;
                    } catch (Exception e) {
                        throw new RuntimeException("Error combining results", e);
                    }
                });
    }
}
