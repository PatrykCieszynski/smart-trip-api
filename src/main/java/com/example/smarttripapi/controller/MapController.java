package com.example.smarttripapi.controller;

import com.example.smarttripapi.dto.internal.CityAutocompleteResponse;
import com.example.smarttripapi.service.MapService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/map/")
@Slf4j
public class MapController {

    private final MapService mapService;

    @GetMapping("/cities/autocomplete")
    public ResponseEntity<List<CityAutocompleteResponse>> searchCities(
            @RequestParam("query")
            @Size(min = 1, max = 100, message = "Query musi mieć od 1 do 100 znaków")
            String query) {

        log.info("Searching cities for query: {}", query);

        List<CityAutocompleteResponse> cities = mapService.searchCities(query);

        log.info("Found {} cities for query '{}'", cities.size(), query);

        return ResponseEntity.ok(cities);
    }

    @GetMapping("/cities/name-by-coords")
    public ResponseEntity<CityAutocompleteResponse> getLocationName(
            @RequestParam("lat") String lat,
            @RequestParam("lng") String lng) {
        CityAutocompleteResponse locationName = mapService.getLocationNameByCoords(lat, lng);
        return ResponseEntity.ok(locationName);
    }

}
