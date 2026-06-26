package com.worldscope.worldscope.service;

import com.worldscope.worldscope.model.Country;
import com.worldscope.worldscope.model.CountryResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.List;

@Service
public class CountryService {

    private final RestTemplate restTemplate;

    @Value("${restcountries.api.url}")
    private String apiUrl;

    @Value("${restcountries.api.key}")
    private String apiKey;

    public CountryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private HttpEntity<Void> buildRequest() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        return new HttpEntity<>(headers);
    }

    public List<Country> getAllCountries() {
        ResponseEntity<CountryResponse> response = restTemplate.exchange(
                apiUrl + "?limit=100",
                HttpMethod.GET,
                buildRequest(),
                CountryResponse.class
        );
        return response.getBody().getData().getObjects();
    }

    public List<Country> searchCountries(String query) {
        ResponseEntity<CountryResponse> response = restTemplate.exchange(
                apiUrl + "?q=" + query + "&limit=100",
                HttpMethod.GET,
                buildRequest(),
                CountryResponse.class
        );
        return response.getBody().getData().getObjects();
    }

    public Country getCountryByName(String name) {
        ResponseEntity<CountryResponse> response = restTemplate.exchange(
                apiUrl + "?q=" + name + "&limit=1",
                HttpMethod.GET,
                buildRequest(),
                CountryResponse.class
        );
        List<Country> results = response.getBody().getData().getObjects();
        return results.isEmpty() ? null : results.get(0);
    }
}