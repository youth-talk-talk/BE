package com.server.youthtalktalk.global.config;

import com.server.youthtalktalk.domain.scrap.dto.PolicyScrapInfoDto;
import com.server.youthtalktalk.domain.scrap.repository.ScrapRepository;
import com.server.youthtalktalk.global.batch.DeleteImgTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import java.util.Collections;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(BatchProperties.class)
public class BatchConfig {
    private final DeleteImgTasklet deleteImgTasklet;
    private final ScrapRepository scrapRepository;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.batch.job", name = "enabled", havingValue = "true", matchIfMissing = true)
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer,
                                                                     JobRepository jobRepository, BatchProperties properties) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
        String jobNames = properties.getJob().getName();
        if (StringUtils.hasText(jobNames)) {
            runner.setJobName(jobNames);
        }
        return runner;
    }

    // S3 이미지 삭제 스케줄링(Tasklet 기반)
    @Bean
    public Job deleteImgJob(JobRepository jobRepository, PlatformTransactionManager transactionManager){
        return new JobBuilder("deleteImgJob", jobRepository)
                .start(deleteImgStep(jobRepository, transactionManager))
                .build();
    }

    @Bean
    public Step deleteImgStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("deleteImgStep", jobRepository)
                .tasklet(deleteImgTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
}
