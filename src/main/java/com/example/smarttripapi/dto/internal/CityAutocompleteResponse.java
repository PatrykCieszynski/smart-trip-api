package com.example.smarttripapi.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityAutocompleteResponse {
    private String id;
    private String name;
    private Double longitude;
    private Double latitude;
    private String type;         // "city", "town", "village"
}
