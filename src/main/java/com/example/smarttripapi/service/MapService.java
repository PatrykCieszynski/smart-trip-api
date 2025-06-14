package com.example.smarttripapi.service;

import com.example.smarttripapi.dto.api.RouteRequest;
import com.example.smarttripapi.dto.external.MapTilerCityResponse;
import com.example.smarttripapi.dto.external.MapTilerFeature;
import com.example.smarttripapi.dto.external.OpenRouteServiceGeojsonResponse;
import com.example.smarttripapi.dto.internal.CityAutocompleteResponse;
import com.example.smarttripapi.dto.internal.RouteResponse;
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

    @Value("${openrouteservice.api.key:}")
    private String openrouteserviceApiKey;

    private static final String MAPTILER_GEOCODING_HOST = "api.maptiler.com";
    private static final String OPENROUTESERVICES_HOST = "api.openrouteservice.org";

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

    public CityAutocompleteResponse getLocationNameByCoords(String lat, String lng) {
        if (lat == null || lng == null) {
            return null;
        }
        try {
            log.info("Calling MapTiler API for coordinates: {}, {}", lat, lng);

            MapTilerCityResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host(MAPTILER_GEOCODING_HOST)
                            .path("/geocoding/{lng},{lat}.json")
                            .queryParam("key", maptilerApiKey)
                            .queryParam("language", "pl")
                            .build(lng.trim(),lat.trim()))
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
                return null;
            }

            List<MapTilerFeature> cities = response.getFeatures();
            log.info("Found {} places for coordinates: {}, {}", cities.size(), lat, lng);
            return mapToSimpleCity(cities.getFirst());
        } catch (Exception e) {
            log.error("Error calling MapTiler API for coords lat '{}', lng '{}: {}", lat, lng, e.getMessage(), e);
            return null;
        }
    }

    public byte[] getTileFromMapTiler(int z, int x, int y) {
        try {
            log.info("Calling MapTiler API for tile: {}, {}, {}", z, x, y);

            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host(MAPTILER_GEOCODING_HOST)
                            .path("/maps/basic-v2/{z}/{x}/{y}.png")
                            .queryParam("key", maptilerApiKey)
                            .build(z, x, y))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, responseError) ->
                            log.error("4xx error from MapTiler API: {}", responseError.getStatusText()))
                    .body(byte[].class);
        }  catch (Exception e) {
            log.error("Error calling MapTiler API for tile: {}, {}, {}: {}", z, x, y, e.getMessage(), e);
            return null;
        }
    }

    public RouteResponse getRoute(RouteRequest request) {
        RouteRequest newRequest = new RouteRequest(
                request.coordinates(),
                1000 // ustawiamy radiuses na 1000
        );

        try {

            log.info("Calling OpenRouteService API for request: {}", newRequest);

            OpenRouteServiceGeojsonResponse response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host(OPENROUTESERVICES_HOST)
                            .path("/v2/directions/driving-car/geojson")
                            .build())
                    .header("Authorization", "Bearer " + openrouteserviceApiKey)  // DODANE "Bearer "
                    .header("Content-Type", "application/json")
                    .body(newRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request1, responseError) ->
                            log.error("4xx error from OpenRouteService API: {}", responseError.getStatusText()))
                    .body(OpenRouteServiceGeojsonResponse.class);

            return mapToRouteResponse(response);
        } catch (Exception e) {
            log.error("Error calling OpenRouteService API for request {}: {}", newRequest, e.getMessage(), e);
            return null;
        }
    }

    private List<CityAutocompleteResponse> mapToSimpleResponse(List<MapTilerFeature> features) {
        return features.stream()
                .filter(this::isCityOrTown)
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
                .name(feature.getTextPl() != null ? feature.getPlaceNamePl() : feature.getPlaceName())
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

    private RouteResponse mapToRouteResponse(OpenRouteServiceGeojsonResponse openRouteResponse) {
        if (openRouteResponse == null || openRouteResponse.features().isEmpty()) {
            return null;
        }

        var feature = openRouteResponse.features().getFirst();
        var properties = feature.properties();
        var summary = properties.summary();
        var geometry = feature.geometry();
        var metadata = openRouteResponse.metadata();

        List<RouteResponse.RoutePoint> routePoints = geometry.coordinates().stream()
                .map(coord -> new RouteResponse.RoutePoint(coord.getFirst(), coord.get(1)))
                .toList();

        RouteResponse.RouteQuery routeQuery = null;
        if (metadata != null && metadata.query() != null) {
            var originalQuery = metadata.query();
            List<RouteResponse.RoutePoint> queryPoints = originalQuery.coordinates().stream()
                    .map(coord -> new RouteResponse.RoutePoint(coord.getFirst(), coord.get(1)))
                    .toList();
            routeQuery = new RouteResponse.RouteQuery(queryPoints);
        }

        return new RouteResponse(
                summary.distance(),
                summary.duration(),
                routePoints,
                routeQuery
        );
    }

}
