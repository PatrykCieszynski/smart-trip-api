package com.example.smarttripapi.service;

import com.example.smarttripapi.dto.AiResponse;
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

    private static final String SYSTEM_PROMPT = """
        Jesteś ekspertem od podróży i turystyki.
        Pomagasz użytkownikom planować wycieczki samochodowe, wytyczasz atrakcje.
        Trasy wygeneruje inny endpoint używając OpenRouteAPI, jedyne co to potrzebuje to nazwy miejsca i jego współrzędnych.
        Odpowiadaj po polsku, bądź konkretny.
        """;
//      Uwzględniaj budżet, preferencje i praktyczne aspekty podróży.

    public AiResponse askQuestion(String question) {
        try {
            log.info("Processing AI question: {}", question);

            Prompt prompt = new Prompt(List.of(
                    new SystemMessage(SYSTEM_PROMPT),
                    new UserMessage(question)
            ));

            String answer = chatModel.call(prompt)
                    .getResult()
                    .getOutput()
                    .getText();

            log.info("AI response generated successfully");

            return AiResponse.builder()
                    .answer(answer)
                    .success(true)
                    .build();

        } catch (Exception e) {
            log.error("Error calling OpenAI API: {}", e.getMessage(), e);

            return AiResponse.builder()
                    .answer("Przepraszam, wystąpił błąd podczas przetwarzania zapytania. Spróbuj ponownie.")
                    .success(false)
                    .build();
        }
    }
}
