package com.example.mirai.projectname.releasepackageservice.releasepackage.service;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration("mychangeReleasePackageServiceConfigurationProperties")
@ConfigurationProperties(prefix = "projectname.releasepackage-service")
public class ReleasePackageServiceConfigurationProperties {
    /**
     * CUG names authorized to perform write operations on form
     */
    String[] TibcoRoles = new String[]{"ROLE_tibco"};


}
