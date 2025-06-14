package com.example.smarttripapi.service;

import com.example.smarttripapi.dto.AiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiAssistantService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
        WAŻNE: Zwróć odpowiedź WYŁĄCZNIE jako obiekt JSON w poniższym formacie

        {
          "answer": "Tekstowa odpowiedź dla użytkownika po polsku",
          "success": true,
          "suggestions": [
            {
              "title": "Nazwa atrakcji/miejsca",
              "description": "Opis co można tam robić",
              "type": "attraction|restaurant|accommodation|activity",
              "location": {
                "name": "Dokładna nazwa miejsca",
                "address": "Adres lub opis lokalizacji",
                "latitude": 00.00000000,
                "longitude": 00.00000000,
              },
              "estimatedCost": "Szacunkowy koszt w PLN",
              "estimatedDuration": "Czas potrzebny na zwiedzanie",
            }
          ],
          ],
          "metadata": {
            "estimatedDuration": "Czas trwania wycieczki",
            "transportMode": "car",
          }
          "locations": [
          {
            "name": "Nazwa miejsca",
            "latitude": 00.00000000,
            "longitude": 00.00000000,
          }
        }
        
        Jesteś ekspertem od podróży i turystyki.
        Pomagasz użytkownikom planować wycieczki, atrakcje.
        Na ten moment załóż że użytkownik podróżuje samochodem.
        Atrakcje powinny być po drodze, ale użytkownik może lekko zboczyć z trasy.
        Optymalna droga do atrakcji będzie wytyczona przez OpenRouteService
        
        Zawsze zwracaj poprawny JSON, w locations podaj wszystkie miejsca na trasie, włącznie z startowym i końcowym według kolejności wycieczki.
        """;

    public AiResponse askQuestion(String question) {
        try {
            log.info("Processing AI question: {}", question);

            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(SYSTEM_PROMPT),
                    new UserMessage(question)
            ));

            String aiResponseText = chatModel.call(prompt)
                    .getResult()
                    .getOutput()
                    .getText();

            log.info("Raw AI response: {}", aiResponseText);

            // Parsuj JSON response z AI
            try {
                // Wyczyść response z markdown formatowania jeśli jest
                String cleanJson = aiResponseText.trim();
                if (cleanJson.startsWith("```json")) {
                    cleanJson = cleanJson.substring(7);
                }
                if (cleanJson.endsWith("```")) {
                    cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
                }
                cleanJson = cleanJson.trim();

                AiResponse structuredResponse = objectMapper.readValue(cleanJson, AiResponse.class);

                log.info("AI response parsed successfully with {} suggestions",
                        structuredResponse.getSuggestions() != null ? structuredResponse.getSuggestions().size() : 0);

                return structuredResponse;

            } catch (JsonProcessingException e) {
                log.error("Failed to parse AI response as JSON: {}", e.getMessage());

                // Fallback - zwróć prostą odpowiedź
                return AiResponse.builder()
                        .answer(aiResponseText)
                        .success(true)
                        .suggestions(List.of())
                        .locations(List.of())
                        .build();
            }

        } catch (Exception e) {
            log.error("Error calling AI API: {}", e.getMessage(), e);

            return AiResponse.builder()
                    .answer("Przepraszam, wystąpił błąd podczas przetwarzania zapytania. Spróbuj ponownie.")
                    .success(false)
                    .errorMessage(e.getMessage())
                    .build();
        }
    }
}