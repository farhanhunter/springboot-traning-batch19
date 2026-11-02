package com.bootcamp19.calculatewordservice.controller;

import com.bootcamp19.calculatewordservice.dto.response.BaseResponse;
import com.bootcamp19.calculatewordservice.dto.response.ResReportDto;
import com.bootcamp19.calculatewordservice.service.ReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/v1/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /** Buat laporan sekarang juga (tanpa menunggu scheduler) */
    @PostMapping("/generate")
    public ResponseEntity<BaseResponse<ResReportDto>> generate() {
        return ResponseEntity.ok(
                BaseResponse.<ResReportDto>builder()
                        .message("Laporan berhasil dibuat")
                        .data(reportService.generateDailyReport())
                        .build()
        );
    }

    /** Presigned URL: link download sementara langsung ke MinIO */
    @GetMapping("/{fileName}/url")
    public ResponseEntity<BaseResponse<String>> downloadUrl(@PathVariable String fileName) {
        return ResponseEntity.ok(
                BaseResponse.<String>builder()
                        .data(reportService.getDownloadUrl(fileName))
                        .build()
        );
    }

    /** Download lewat aplikasi (file di-stream dari MinIO) */
    @GetMapping("/{fileName}/download")
    public void download(@PathVariable String fileName, HttpServletResponse response) {
        reportService.downloadReport(fileName, response);
    }
}
