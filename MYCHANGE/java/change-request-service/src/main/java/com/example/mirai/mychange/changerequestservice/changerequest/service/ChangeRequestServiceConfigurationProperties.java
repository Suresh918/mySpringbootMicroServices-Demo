package com.example.mirai.projectname.changerequestservice.changerequest.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration("mychangeChangeRequestServiceConfigurationProperties")
@ConfigurationProperties(prefix = "projectname.changerequest-service")
public class ChangeRequestServiceConfigurationProperties {
    /**
     * CUG names authorized to perform write operations on form
     */
    String[] tibcoRoles = new String[]{"ROLE_tibco"};

    String[] adminRoles = new String[]{"ROLE_administrator"};


}
