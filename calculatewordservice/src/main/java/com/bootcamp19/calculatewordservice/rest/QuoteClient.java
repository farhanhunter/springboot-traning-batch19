package com.bootcamp19.calculatewordservice.rest;

import com.bootcamp19.calculatewordservice.dto.feign.ResQuoteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Feign ke API pihak ketiga: pakai url (bukan nama service Eureka).
 */
@FeignClient(name = "quoteClient", url = "${quote.api.url}")
public interface QuoteClient {

    @GetMapping("/quotes/random")
    ResQuoteDto getRandomQuote();
}
