package com.example.mirai.projectname.reviewservice.review.listener;

import com.example.mirai.libraries.deltareport.service.DeltaReportService;
import com.example.mirai.projectname.libraries.model.ReleasePackage;
import com.example.mirai.projectname.reviewservice.review.service.ReviewService;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;

@Component
@Slf4j
public class Listener {

    private ReviewService reviewService;
    private DeltaReportService deltaReportService;

    public Listener(ReviewService reviewService,DeltaReportService deltaReportService) {
        this.reviewService = reviewService;
        this.deltaReportService = deltaReportService;
    }

    @JmsListener(destination = "com.example.mirai.projectname.reviewservice.releasepackage")
    public void processReleasePackageUpdated(final Message message) throws JMSException {
        ReleasePackage releasePackage = new ReleasePackage(message.getBody(String.class), "release_package", "REVIEW");
        reviewService.updateReleasePackageStatus(releasePackage);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.reviewservice.deltareport")
    public void processDeltaReport(final Message message) throws JMSException, JsonProcessingException {
        deltaReportService.processDeltaReport(message);
        message.acknowledge();
    }
}
