package com.example.smarttripapi.controller;

import com.example.smarttripapi.dto.api.RouteRequest;
import com.example.smarttripapi.dto.internal.RouteWithAiSuggestionsResponse;
import com.example.smarttripapi.service.TripPlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trip")
@RequiredArgsConstructor
public class TripController {
    private final TripPlanningService tripPlanningService;

    @PostMapping("/route-with-ai")
    public ResponseEntity<RouteWithAiSuggestionsResponse> getRouteWithAi(@RequestBody RouteRequest request) {
        return ResponseEntity.ok(tripPlanningService.getRouteWithAiSuggestions(request));
    }
}
