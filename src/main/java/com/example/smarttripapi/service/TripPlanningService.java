package com.example.smarttripapi.service;

import com.example.smarttripapi.dto.AiResponse;
import com.example.smarttripapi.dto.internal.RouteResponse;
import com.example.smarttripapi.dto.internal.RouteWithAiSuggestionsResponse;
import com.example.smarttripapi.dto.api.RouteRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripPlanningService {

    private final AiAssistantService aiAssistantService;
    private final MapService mapService;

    public RouteWithAiSuggestionsResponse getRouteWithAiSuggestions(RouteRequest request) {
        // Prompt do AI (możesz dodać np. informacje geograficzne)
        String prompt = "Wygeneruj inspiracje i atrakcje na trasie dla następujących punktów współrzędnych: " + request.coordinates();

        AiResponse aiResponse = aiAssistantService.askQuestion(prompt);

        // Stwórz nowy RouteRequest z punktów które są w aiResponse locations
        List<List<Double>> coordinatesFromLocations = aiResponse.getLocations()
                .stream()
                .map(loc -> List.of(loc.getLongitude(), loc.getLatitude()))
                .toList();
        RouteRequest updatedRequest = new RouteRequest(coordinatesFromLocations, 1000);

        RouteResponse route = mapService.getRoute(updatedRequest);

        return new RouteWithAiSuggestionsResponse(route, aiResponse);
    }

}
