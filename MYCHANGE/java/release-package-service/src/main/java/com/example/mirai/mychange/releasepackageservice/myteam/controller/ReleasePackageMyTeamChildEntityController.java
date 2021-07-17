package com.example.mirai.projectname.releasepackageservice.myteam.controller;

import com.example.mirai.libraries.myteam.controller.MyTeamChildEntityController;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.myteam.service.ReleasePackageMyTeamService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:release-packages}/{parentId:[0-9]+}/{entityType:my-team}")
public class ReleasePackageMyTeamChildEntityController extends MyTeamChildEntityController {

    ReleasePackageMyTeamService releasePackageMyTeamService;
    ReleasePackageService releasePackageService;


    public ReleasePackageMyTeamChildEntityController(ObjectMapper objectMapper, ReleasePackageMyTeamService releasePackageMyTeamService,
                                                     ReleasePackageService releasePackageService, EntityResolver entityResolver) {
        super(objectMapper, releasePackageMyTeamService, entityResolver);
        this.releasePackageMyTeamService=releasePackageMyTeamService;
        this.releasePackageService=releasePackageService;
    }
}

