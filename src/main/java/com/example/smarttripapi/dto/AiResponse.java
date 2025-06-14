package com.example.smarttripapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiResponse {
    private String answer;
    private boolean success;
    private String errorMessage;

    private List<TripSuggestion> suggestions;
    private List<LocationPoint> locations;
    private TripMetadata metadata;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripSuggestion {
        private String title;
        private String description;
        private String type; // "attraction", "restaurant", "accommodation", "activity"
        private LocationPoint locations;
        private String estimatedCost;
        private String estimatedDuration;
        private Integer priority; // 1-5, gdzie 1 = najważniejsze
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LocationPoint {
        private String name;
        private String address;
        private Double latitude;
        private Double longitude;
        private String locationType; // "city", "attraction", "restaurant", etc.
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TripMetadata {
        private String estimatedBudget;
        private String estimatedDuration;
        private String transportMode; // "car", "walking", "public_transport"
        private List<String> tags; // ["family-friendly", "budget", "luxury", "adventure"]
        private String season; // "spring", "summer", "autumn", "winter", "any"
    }
}