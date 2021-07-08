package com.example.mirai.libraries.backgroundable.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Data
@ConfigurationProperties(prefix = "mirai.libraries.backgroundable.job-owner-groups")
public class JobOwnerGroupConfigurationProperties {
    List<String> primaryGroups;
}
