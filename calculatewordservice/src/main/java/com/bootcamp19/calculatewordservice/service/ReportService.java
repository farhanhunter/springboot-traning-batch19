package com.bootcamp19.calculatewordservice.service;

import com.bootcamp19.calculatewordservice.dto.response.ResReportDto;
import jakarta.servlet.http.HttpServletResponse;

public interface ReportService {
    ResReportDto generateDailyReport();
    String getDownloadUrl(String fileName);
    void downloadReport(String fileName, HttpServletResponse response);
}
