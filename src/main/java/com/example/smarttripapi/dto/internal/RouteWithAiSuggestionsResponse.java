package com.example.smarttripapi.dto.internal;

import com.example.smarttripapi.dto.AiResponse;

public record RouteWithAiSuggestionsResponse(
        RouteResponse route,
        AiResponse ai
) {

}
