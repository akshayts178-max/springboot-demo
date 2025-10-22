package com.example.demo.repo;

import com.example.demo.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {
    //Basic string matching
    List<Address> findByCity(String city);

    List<Address> findByCountry(String country);

    List<Address> findByZipcode(String zipcode);

    //Multiple conditions
    List<Address> findByCityAndCountry(String city, String country);

    List<Address> findByCityOrZipcode(String city, String zipcode);

    //Like Queries
    List<Address> findByStreetContaining(String keyword);

    List<Address> findByStreetNameStartingWith(String prefix);

    List<Address> findByStreetNameEndingWith(String suffix);

    //Comparison on numeric fields
    List<Address> findByLatitudeBetween(Double min, Double max);

    List<Address> findByLongitudeLessThan(Double value);

    List<Address> findByLatitudeGreaterThan(Double value);

    //Predicted info queries
    List<Address> findByPredictedGender(String gender);

    List<Address> findByPredictedAgeGreaterThan(Integer age);

    List<Address> findByGenderProbabilityBetween(Double min, Double max);

    //Distinct / Ordering / Top-N
    List<Address> findDistinctByCity(String city);

    List<Address> findTop10ByCountryOrderByPredictedAgeDesc(String country);

    //Case-insensitive search
    List<Address> findByCityIgnoreCase(String city);

    List<Address> findByCountryContainingIgnoreCase(String country);

    //Optional return
    Optional<Address> findByZipcodeAndBuildingNumber(String zipcode, String buildingNumber);

    boolean existsByCity(String city);

    long countByCountry(String country);
}
