package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.libraries.core.model.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PmoDetails {
    private String projectId;
    private User projectLead;
    private User projectClusterManager;
    private User productDevelopmentManager;
    private String description;
}
