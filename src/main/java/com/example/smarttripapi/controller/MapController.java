package com.example.smarttripapi.controller;

import com.example.smarttripapi.dto.api.RouteRequest;
import com.example.smarttripapi.dto.internal.CityAutocompleteResponse;
import com.example.smarttripapi.dto.internal.RouteResponse;
import com.example.smarttripapi.service.MapService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @GetMapping("/{z}/{x}/{y}.png")
    @Cacheable(value = "tiles", key = "#z + '_' + #x + '_' + #y")
    public ResponseEntity<byte[]> getTile(
            @PathVariable @Valid @Min(0)
            int z,
            @PathVariable @Valid @Min(0)
            int x,
            @PathVariable @Valid @Min(0)
            int y) {
        byte[] tileData = mapService.getTileFromMapTiler(z, x, y);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.maxAge(24, TimeUnit.HOURS))
                .body(tileData);
    }


    @PostMapping("/route")
    public ResponseEntity<RouteResponse> getRoute(@RequestBody RouteRequest request) {
        RouteResponse response = mapService.getRoute(request);
        return ResponseEntity.ok(response);
    }
}
