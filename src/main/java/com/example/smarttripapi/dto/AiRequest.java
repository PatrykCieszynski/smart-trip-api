package com.example.smarttripapi.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiRequest {
    @NotBlank(message = "Pytanie nie może być puste")
    @Size(max = 1000, message = "Pytanie nie może być dłuższe niż 1000 znaków")
    private String question;
}
