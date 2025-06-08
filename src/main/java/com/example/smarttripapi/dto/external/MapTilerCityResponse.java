package com.example.smarttripapi.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapTilerCityResponse {
    private String type; // "FeatureCollection"
    private List<MapTilerFeature> features;
    private List<String> query;
    private String attribution;
}
