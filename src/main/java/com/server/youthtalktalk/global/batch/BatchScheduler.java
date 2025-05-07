package com.server.youthtalktalk.global.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Configuration
public class BatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job deleteImgJob;

    @Async(value = "asyncTask")
    @Scheduled(cron = "0 0 0 * * SUN") // 매주 일요일 자정 실행
    public void runDeleteImgJob()
            throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException, JobRestartException {
        jobLauncher.run(deleteImgJob, new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis()) // 유니크 파라미터 추가
                .toJobParameters());
    }
}
