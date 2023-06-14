package com.metanet.metabus.config;

import com.metanet.metabus.bus.scheduler.MyJob;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetailFactoryBean jobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(MyJob.class);
        jobDetailFactory.setDurability(true);
        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean trigger(JobDetail jobDetail) {
        SimpleTriggerFactoryBean triggerFactory = new SimpleTriggerFactoryBean();
        triggerFactory.setJobDetail(jobDetail);
        triggerFactory.setStartDelay(60000); // 1분 (단위: 밀리초)
        triggerFactory.setRepeatInterval(60000); // 1분 (단위: 밀리초)
        return triggerFactory;
    }

    @Bean
    public CronTriggerFactoryBean dailyTrigger(JobDetail jobDetail) {
        CronTriggerFactoryBean triggerFactory = new CronTriggerFactoryBean();
        triggerFactory.setJobDetail(jobDetail);
        triggerFactory.setCronExpression("0 0 12 * * ?"); // 매일 정오에 실행
        return triggerFactory;
    }

}
