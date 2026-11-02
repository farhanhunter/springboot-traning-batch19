package com.bootcamp19.calculatewordservice.dto.feign;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response dari https://dummyjson.com/quotes/random
 * { "id": 1, "quote": "...", "author": "..." }
 */
@JsonIgnoreProperties(ignoreUnknown = true) // abaikan field yang tidak kita butuhkan
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResQuoteDto {
    private Long id;
    private String quote;
    private String author;
}
