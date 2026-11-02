package com.bootcamp19.userservice.controller;

import com.bootcamp19.userservice.dto.response.BaseResponse;
import com.bootcamp19.userservice.dto.response.ResFeignAnalysisDto;
import com.bootcamp19.userservice.service.AnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contoh service-to-service: userservice memanggil calculatewordservice lewat Feign.
 */
@RestController
@RequestMapping("/rest/v1/analyses")
public class AnalysisController {
    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ResFeignAnalysisDto>> analysisById(@PathVariable Long id) {
        return ResponseEntity.ok(
                BaseResponse.<ResFeignAnalysisDto>builder()
                        .message("Success get analysis via Feign")
                        .data(analysisService.getAnalysisById(id))
                        .build()
        );
    }
}
