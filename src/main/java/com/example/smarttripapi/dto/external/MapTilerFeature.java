package com.example.smarttripapi.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MapTilerFeature {
    private String type;
    private Properties properties;
    private Geometry geometry;
    private List<Double> bbox;
    private List<Double> center;

    @JsonProperty("place_name")
    private String placeName;

    @JsonProperty("place_name_pl")
    private String placeNamePl;

    @JsonProperty("place_type")
    private List<String> placeType;

    private Double relevance;
    private String id;
    private String text;

    @JsonProperty("text_pl")
    private String textPl;

    private String language;
    private List<Context> context;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Properties {
        private String ref;

        @JsonProperty("country_code")
        private String countryCode;

        private String wikidata;
        private String kind;

        @JsonProperty("place_type_name")
        private List<String> placeTypeName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Geometry {
        private String type; // "Point"
        private List<Double> coordinates; // [longitude, latitude]
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Context {
        private String ref;
        private String id;
        private String text;

        @JsonProperty("text_pl")
        private String textPl;

        @JsonProperty("country_code")
        private String countryCode;

        private String wikidata;
        private String kind;
        private String language;
        private List<String> categories;

        @JsonProperty("osm:tags")
        private Map<String, Object> osmTags;
    }
}