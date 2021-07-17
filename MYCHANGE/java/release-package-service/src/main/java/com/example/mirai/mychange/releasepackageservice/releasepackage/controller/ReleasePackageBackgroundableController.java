package com.example.mirai.projectname.releasepackageservice.releasepackage.controller;


import java.util.Arrays;
import java.util.List;

import com.example.mirai.libraries.backgroundable.model.dto.CategorizedCount;
import com.example.mirai.libraries.backgroundable.model.dto.CategorizedJob;
import com.example.mirai.libraries.backgroundable.service.JobService;
import lombok.Data;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
public class ReleasePackageBackgroundableController {

    private final JobService jobService;

    @GetMapping(value = "release-packages/jobs", params = "view=categorized")
	public CategorizedJob getCategorizedJobs() {
        List<String> parentNames = Arrays.asList(new String[]{"RELEASEPACKAGE", "MYTEAM"});
        return jobService.getCategorizedJob(parentNames);
    }

    @GetMapping(value = "release-packages/jobs", params = "view=categorized-count")
	public CategorizedCount getCategorizedJobCount() {
        return jobService.getCategorizedCount("RELEASEPACKAGE");
    }
}
