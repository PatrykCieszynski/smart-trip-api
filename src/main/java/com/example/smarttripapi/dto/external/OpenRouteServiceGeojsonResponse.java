package com.example.smarttripapi.dto.external;

import java.util.List;

public record OpenRouteServiceGeojsonResponse(
        String type,
        List<Double> bbox,
        List<Feature> features,
        Metadata metadata
) {

    public record Feature(
            List<Double> bbox,
            String type,
            Properties properties,
            Geometry geometry
    ) {}

    public record Properties(
            List<Segment> segments,
            Summary summary,
            List<Integer> wayPoints
    ) {}

    public record Segment(
            Double distance,
            Double duration,
            List<Step> steps
    ) {}

    public record Step(
            Double distance,
            Double duration,
            Integer type,
            String instruction,
            String name,
            List<Integer> wayPoints
    ) {}

    public record Summary(
            Double distance,
            Double duration
    ) {}

    public record Geometry(
            List<List<Double>> coordinates,
            String type
    ) {}

    public record Metadata(
            String attribution,
            String service,
            Long timestamp,
            Query query,
            Engine engine
    ) {}

    public record Query(
            List<List<Double>> coordinates,
            String profile,
            String profileName,
            String format
    ) {}

    public record Engine(
            String version,
            String buildDate,
            String graphDate
    ) {}
}
