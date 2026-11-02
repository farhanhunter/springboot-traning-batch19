package com.bootcamp19.userservice.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SchedulerTest {

    // jadwal diambil dari application.yaml supaya bisa diubah tanpa compile ulang
    @Scheduled(cron = "${scheduler.test.cron}")
    public void testScheduler() {
        log.info("[SchedulerTest] userservice masih hidup");
    }
}
