package com.example.demo.domain;

import com.example.demo.model.Address;
import com.example.demo.repo.AddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {
    @Mock
    private AddressRepository addressRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(addressService, "restTemplate", restTemplate);
    }

    @Test
    void testFetchAddressAsync_success() throws Exception {
        Map<String, Object> addrMap = Map.of(
                "street", "123 Main st",
                "streetName", "Main",
                "buildingNumber", "42",
                "city", "springfield",
                "zipcode", "12345",
                "country", "USA"
        );

        Map<String, Object> response = Map.of("data", List.of(addrMap));
        when(restTemplate.getForObject(anyString(), eq(Map.class))).thenReturn(response);
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));

        CompletableFuture<Address> future = addressService.fetchAddressAsync();
        Address address = future.get();

        assertEquals("springfield", address.getCity());
        verify(addressRepository, times(1)).save(any(Address.class));
    }

    @Test
    void testFetchGenderizeAsync_success() throws Exception {
        Map<String, Object> mockResponse = Map.of("name", "luc", "gender", "male");
        when(restTemplate.getForObject(contains("genderize"), eq(Map.class))).thenReturn(mockResponse);

        CompletableFuture<Map<String, Object>> future = addressService.fetchGenderizeAsync();
        Map<String, Object> result = future.get();

        assertEquals("male", result.get("gender"));
    }

    @Test
    void testFetchAgifyAsync_success() throws Exception {
        Map<String, Object> mockResponse = Map.of("name", "meelad", "age", 35);
        when(restTemplate.getForObject(contains("agify"), eq(Map.class))).thenReturn(mockResponse);

        CompletableFuture<Map<String, Object>> future = addressService.fetchAgifyAsync();
        Map<String, Object> result = future.get();

        assertEquals(35, result.get("age"));
    }

    @Test
    void testFetchAllAsync_success() throws Exception {
        Map<String, Object> addrMap = Map.of(
                "street", "123 Main st",
                "streetName", "Main",
                "buildingNumber", "42",
                "city", "SpringField",
                "zipcode", "12345",
                "country", "USA"
        );
        Map<String, Object> addressResponse = Map.of("data", List.of(addrMap));

        Map<String, Object> agifyResponse = Map.of("age", 30);
        Map<String, Object> genderizeResponse = Map.of("gender", "male", "probability", 0.95);

        when(restTemplate.getForObject(contains("fakerapi"), eq(Map.class))).thenReturn(addressResponse);
        when(restTemplate.getForObject(contains("agify"), eq(Map.class))).thenReturn(agifyResponse);
        when(restTemplate.getForObject(contains("genderize"), eq(Map.class))).thenReturn(genderizeResponse);
        when(addressRepository.save(any(Address.class))).thenAnswer(i -> i.getArgument(0));

        CompletableFuture<Map<String, Object>> future = addressService.fetchAllAsync();
        Map<String, Object> result = future.get();

        Address address = (Address) result.get("address");
        assertEquals("male", address.getPredictedGender());
        assertEquals(30, address.getPredictedAge());
        assertEquals(0.95, address.getGenderProbability());
    }
}