package com.example.mirai.projectname.releasepackageservice;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
@EnableJpaRepositories(basePackages = "com.example.mirai")
@EntityScan(basePackages = "com.example.mirai")
@ConfigurationPropertiesScan(basePackages = "com.example.mirai")
@ComponentScan(basePackages = "com.example.mirai")
@EnableCaching
@EnableJpaAuditing
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT5M", defaultLockAtLeastFor = "PT1M", interceptMode = EnableSchedulerLock.InterceptMode.PROXY_SCHEDULER)
@SpringBootApplication()
public class ReleasePackageServiceApplication {

    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(ReleasePackageServiceApplication.class, args);
    }

    // to capture dates in UTC timezone
    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
    }

}
