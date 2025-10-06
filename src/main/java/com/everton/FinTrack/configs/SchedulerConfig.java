package com.everton.FinTrack.configs;

import com.everton.FinTrack.services.DataCleanupService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    private final DataCleanupService cleanupService;

    public SchedulerConfig(DataCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }

    @Scheduled(cron = "0 0 0 1 2 *") // 1º de fevereiro, meia-noite
    public void runCleanup() {
        int lastYear = LocalDate.now().getYear() - 1;
        cleanupService.archiveAndDeleteYear(lastYear);
    }
}
