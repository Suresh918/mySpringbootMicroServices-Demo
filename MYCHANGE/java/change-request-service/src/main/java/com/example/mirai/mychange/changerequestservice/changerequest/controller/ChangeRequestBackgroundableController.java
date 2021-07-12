package com.example.mirai.projectname.changerequestservice.changerequest.controller;


import com.example.mirai.libraries.backgroundable.model.dto.CategorizedCount;
import com.example.mirai.libraries.backgroundable.model.dto.CategorizedJob;
import com.example.mirai.libraries.backgroundable.service.JobService;
import lombok.Data;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{entityType:change-requests}")
@Data
public class ChangeRequestBackgroundableController {

    private final JobService jobService;

    @GetMapping(value = "/jobs", params = "view=categorized")
    public CategorizedJob getCategorizedJobs() {
        return jobService.getCategorizedJob("MYTEAM");
    }

    @GetMapping(value = "/jobs", params = "view=categorized-count")
    public CategorizedCount getCategorizedJobCount() {
        return jobService.getCategorizedCount("MYTEAM");
    }
}
