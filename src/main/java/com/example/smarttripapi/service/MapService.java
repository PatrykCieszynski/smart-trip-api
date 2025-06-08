package com.example.smarttripapi.service;

import com.example.smarttripapi.dto.external.MapTilerCityResponse;
import com.example.smarttripapi.dto.external.MapTilerFeature;
import com.example.smarttripapi.dto.internal.CityAutocompleteResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class MapService {

    private final RestClient restClient;

    @Value("${maptiler.api.key:}")
    private String maptilerApiKey;

    private static final String MAPTILER_GEOCODING_HOST = "api.maptiler.com";

    public List<CityAutocompleteResponse> searchCities(String query) {
        List<MapTilerFeature> rawResults = searchCitiesRaw(query);
        return mapToSimpleResponse(rawResults);
    }

    private List<MapTilerFeature> searchCitiesRaw(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        try {
            log.info("Calling MapTiler API for query: {}", query);

            MapTilerCityResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host(MAPTILER_GEOCODING_HOST)
                            .path("/geocoding/{query}.json")
                            .queryParam("key", maptilerApiKey)
                            .queryParam("language", "pl")
                            .build(query.trim()))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, responseError) -> {
                        log.error("4xx error from MapTiler API: {}", responseError.getStatusText());
                        throw new RuntimeException("Client error: " + responseError.getStatusText());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, responseError) -> {
                        log.error("5xx error from MapTiler API: {}", responseError.getStatusText());
                        throw new RuntimeException("Server error: " + responseError.getStatusText());
                    })
                    .body(MapTilerCityResponse.class);

            if (response == null || response.getFeatures() == null) {
                log.warn("Empty response from MapTiler API");
                return Collections.emptyList();
            }

            List<MapTilerFeature> cities = response.getFeatures();
            log.info("Found {} cities for query '{}'", cities.size(), query);

            return cities;

        } catch (Exception e) {
            log.error("Error calling MapTiler API for query '{}': {}", query, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private List<CityAutocompleteResponse> mapToSimpleResponse(List<MapTilerFeature> features) {
        return features.stream()
                .filter(this::isCityOrTown) // Filtrujemy tylko miasta
                .map(this::mapToSimpleCity)
                .collect(Collectors.toList());
    }

    private boolean isCityOrTown(MapTilerFeature feature) {
        List<String> placeTypes = feature.getPlaceType();
        return placeTypes != null && (
                placeTypes.contains("locality") ||
                        placeTypes.contains("municipality") ||
                        placeTypes.contains("county") ||
                        placeTypes.contains("place")
        );
    }

    private CityAutocompleteResponse mapToSimpleCity(MapTilerFeature feature) {
        String type = getCityType(feature);

        return CityAutocompleteResponse.builder()
                .id(feature.getId())
                .name(feature.getTextPl() != null ? feature.getTextPl() : feature.getText())
                .longitude(feature.getCenter() != null && !feature.getCenter().isEmpty() ?
                        feature.getCenter().get(0) : null)
                .latitude(feature.getCenter() != null && feature.getCenter().size() > 1 ?
                        feature.getCenter().get(1) : null)
                .type(type)
                .build();
    }

    private String getCityType(MapTilerFeature feature) {
        List<String> placeTypes = feature.getPlaceType();
        if (placeTypes != null && !placeTypes.isEmpty()) {
            String primaryType = placeTypes.getFirst();
            return switch (primaryType) {
                case "locality" -> "city";
                case "municipality" -> "municipality";
                case "county" -> "county";
                case "place" -> "place";
                default -> "location";
            };
        }
        return "location";
    }


}
