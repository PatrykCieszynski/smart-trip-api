package com.example.smarttripapi.dto.internal;

import java.util.List;

public record RouteResponse(
        Double totalDistance,
        Double totalDuration,
        List<RoutePoint> geometry,
        RouteQuery query
) {

    public record RoutePoint(
            Double longitude,
            Double latitude
    ) {}

    public record RouteQuery(
            List<RoutePoint> coordinates
    ) {}
}
