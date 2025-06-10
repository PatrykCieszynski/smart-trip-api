package com.example.smarttripapi.dto.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record RouteRequest(
        @NotNull(message = "Coordinates cannot be null")
        @Size(min = 2, max = 50, message = "Must provide between 2 and 50 coordinate pairs")
        List<List<Double>> coordinates
) {
}
