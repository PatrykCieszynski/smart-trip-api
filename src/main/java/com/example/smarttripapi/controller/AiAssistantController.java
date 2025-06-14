package com.example.smarttripapi.controller;

import com.example.smarttripapi.dto.AiRequest;
import com.example.smarttripapi.dto.AiResponse;
import com.example.smarttripapi.service.AiAssistantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai")
public class AiAssistantController {
    private final AiAssistantService aiAssistantService;

    @PostMapping("/ask")
    public ResponseEntity<AiResponse> askQuestion(@Valid @RequestBody AiRequest request) {
        AiResponse response = aiAssistantService.askQuestion(request.getQuestion());
        return ResponseEntity.ok(response);
    }

}
