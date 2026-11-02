package com.bootcamp19.calculatewordservice.service.scheduler;

import com.bootcamp19.calculatewordservice.dto.response.ResReportDto;
import com.bootcamp19.calculatewordservice.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Catatan: kalau service ini jalan 2 instance, scheduler juga jalan 2x.
 * Solusinya pakai lock terdistribusi seperti ShedLock.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DailyReportScheduler {
    private final ReportService reportService;

    @Scheduled(cron = "${report.scheduler.cron}")
    public void generateDailyReport() {
        log.info("[Scheduler] Mulai membuat laporan harian");
        try {
            ResReportDto report = reportService.generateDailyReport();
            log.info("[Scheduler] Laporan selesai: {} ({} data)", report.getFileName(), report.getTotalData());
        } catch (Exception e) {
            // jangan sampai error di scheduler mematikan thread scheduler
            log.error("[Scheduler] Gagal membuat laporan: {}", e.getMessage());
        }
    }
}
