package com.example.mirai.projectname.releasepackageservice.releasepackage.service;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "com.example.mirai.projectname.releasepackageservice.product-attribute-flow")
@Data
public class ReleasePackageConfigurationProperties {

    private String enabled;
}
