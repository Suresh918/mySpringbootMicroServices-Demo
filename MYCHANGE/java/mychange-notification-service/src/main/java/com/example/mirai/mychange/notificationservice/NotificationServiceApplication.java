package com.example.mirai.projectname.notificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@EnableJpaRepositories(basePackages = {"com.example.mirai"})
@EntityScan(basePackages = {"com.example.mirai.libraries.notification"})
@ConfigurationPropertiesScan(basePackages = {"com.example.mirai"})
@ComponentScan(basePackages = {"com.example.mirai"})
@EnableCaching
@SpringBootApplication()
public class NotificationServiceApplication {

    private static ApplicationContext applicationContext;

    public static void main(String[] args) {
        applicationContext = SpringApplication.run(NotificationServiceApplication.class, args);
    }
}
