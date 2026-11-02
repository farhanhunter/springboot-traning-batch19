package com.bootcamp19.calculatewordservice.service.impl;

import com.bootcamp19.calculatewordservice.dto.response.ResReportDto;
import com.bootcamp19.calculatewordservice.entity.AnalysisEntity;
import com.bootcamp19.calculatewordservice.repository.AnalysisRepository;
import com.bootcamp19.calculatewordservice.service.ReportService;
import com.bootcamp19.calculatewordservice.util.MinioUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final AnalysisRepository analysisRepository;
    private final MinioUtil minioUtil;

    /** Buat CSV berisi analisis 24 jam terakhir, lalu upload ke MinIO. */
    @Override
    public ResReportDto generateDailyReport() {
        LocalDateTime to = LocalDateTime.now();
        LocalDateTime from = to.minusDays(1);
        List<AnalysisEntity> data = analysisRepository.findByCreatedAtBetweenOrderByIdAsc(from, to);

        StringBuilder csv = new StringBuilder("id,kalimat,total_huruf,huruf_terbanyak,created_by,created_at\n");
        for (AnalysisEntity a : data) {
            csv.append(a.getId()).append(',')
                    .append('"').append(a.getKalimat().replace("\"", "\"\"")).append('"').append(',')
                    .append(a.getTotalHuruf()).append(',')
                    .append(a.getHurufTerbanyak() == null ? "" : a.getHurufTerbanyak()).append(',')
                    .append(a.getCreatedBy() == null ? "" : a.getCreatedBy()).append(',')
                    .append(a.getCreatedAt()).append('\n');
        }

        String fileName = "report-analysis-"
                + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-"
                + System.currentTimeMillis() + ".csv";

        minioUtil.upload(fileName, csv.toString().getBytes(StandardCharsets.UTF_8), "text/csv");
        log.info("Report {} dibuat dengan {} data", fileName, data.size());

        return ResReportDto.builder()
                .fileName(fileName)
                .totalData(data.size())
                .downloadUrl(minioUtil.getPresignedUrl(fileName))
                .build();
    }

    @Override
    public String getDownloadUrl(String fileName) {
        return minioUtil.getPresignedUrl(fileName);
    }

    @Override
    public void downloadReport(String fileName, HttpServletResponse response) {
        minioUtil.download(fileName, response);
    }
}
