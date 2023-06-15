package com.metanet.metabus.scheduler;

import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetailFactoryBean minuteJobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(MinuteJob.class);
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean minuteTrigger(JobDetail minuteJobDetail) {
        SimpleTriggerFactoryBean triggerFactory = new SimpleTriggerFactoryBean();
        triggerFactory.setJobDetail(minuteJobDetail);
        triggerFactory.setStartDelay(60000); // 1분 (단위: 밀리초)
        triggerFactory.setRepeatInterval(60000); // 1분 (단위: 밀리초)
        return triggerFactory;
    }

    @Bean
    public JobDetailFactoryBean dailyJobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(DailyJob.class);
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    public CronTriggerFactoryBean dailyTrigger(JobDetail dailyJobDetail) {
        CronTriggerFactoryBean triggerFactory = new CronTriggerFactoryBean();
        triggerFactory.setJobDetail(dailyJobDetail);
        triggerFactory.setCronExpression("0 0 12 * * ?"); // 매일 정오에 실행
        return triggerFactory;
    }

}
