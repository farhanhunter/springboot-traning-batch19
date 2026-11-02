package com.bootcamp19.calculatewordservice.controller;

import com.bootcamp19.calculatewordservice.dto.request.ReqCreateAnalysisDto;
import com.bootcamp19.calculatewordservice.dto.response.BaseResponse;
import com.bootcamp19.calculatewordservice.dto.response.ResAnalysisDto;
import com.bootcamp19.calculatewordservice.dto.response.ResTrendingDto;
import com.bootcamp19.calculatewordservice.service.AnalysisService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rest/v1/analyses")
public class AnalysisController {
    private static final String HEADER_USER_ID = "X-User-Id"; // diisi gateway dari token JWT

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /** POST /rest/v1/analyses  body: { "kalimat": "gajah mati meninggalkan gading" } */
    @PostMapping
    public ResponseEntity<BaseResponse<ResAnalysisDto>> createAnalysis(
            @Valid @RequestBody ReqCreateAnalysisDto request,
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.<ResAnalysisDto>builder()
                        .message("Analisis berhasil")
                        .data(analysisService.createAnalysis(request, userId))
                        .build()
        );
    }

    /** POST /rest/v1/analyses/random -> ambil quote dari API pihak ketiga (Feign) lalu dianalisis */
    @PostMapping("/random")
    public ResponseEntity<BaseResponse<ResAnalysisDto>> analyzeRandomQuote(
            @RequestHeader(value = HEADER_USER_ID, required = false) Long userId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponse.<ResAnalysisDto>builder()
                        .message("Analisis quote acak berhasil")
                        .data(analysisService.analyzeRandomQuote(userId))
                        .build()
        );
    }

    /** GET /rest/v1/analyses?huruf=a  (huruf opsional) */
    @GetMapping
    public ResponseEntity<BaseResponse<List<ResAnalysisDto>>> listAnalyses(
            @RequestParam(required = false) String huruf
    ) {
        return ResponseEntity.ok(
                BaseResponse.<List<ResAnalysisDto>>builder()
                        .data(analysisService.getAllAnalyses(huruf))
                        .build()
        );
    }

    /** GET /rest/v1/analyses/me -> analisis milik user yang login */
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<List<ResAnalysisDto>>> myAnalyses(
            @RequestHeader(HEADER_USER_ID) Long userId
    ) {
        return ResponseEntity.ok(
                BaseResponse.<List<ResAnalysisDto>>builder()
                        .data(analysisService.getMyAnalyses(userId))
                        .build()
        );
    }

    /** GET /rest/v1/analyses/trending?limit=5 -> huruf paling sering muncul (dari Redis) */
    @GetMapping("/trending")
    public ResponseEntity<BaseResponse<List<ResTrendingDto>>> trending(
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(
                BaseResponse.<List<ResTrendingDto>>builder()
                        .data(analysisService.getTrendingHuruf(limit))
                        .build()
        );
    }

    /** GET /rest/v1/analyses/{id} -> pakai cache Redis */
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<ResAnalysisDto>> analysisById(@PathVariable Long id) {
        return ResponseEntity.ok(
                BaseResponse.<ResAnalysisDto>builder()
                        .data(analysisService.getAnalysisById(id))
                        .build()
        );
    }

    /** DELETE /rest/v1/analyses/{id} -> hapus data + hapus cache */
    @DeleteMapping("/{id}")
    public ResponseEntity<BaseResponse<Object>> deleteAnalysis(@PathVariable Long id) {
        analysisService.deleteAnalysis(id);
        return ResponseEntity.ok(
                BaseResponse.builder()
                        .message("Analisis dengan id " + id + " dihapus")
                        .build()
        );
    }
}
