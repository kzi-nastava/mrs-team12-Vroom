package org.example.vroom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfiguration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulingConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

        // max number of jobs running at same time, change as needed
        taskScheduler.setPoolSize(3);
        taskScheduler.setThreadNamePrefix("task-scheduler-");
        taskScheduler.initialize();

        taskRegistrar.setTaskScheduler(taskScheduler);
    }
}
