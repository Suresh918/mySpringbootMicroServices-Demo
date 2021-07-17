package com.example.mirai.projectname.notificationservice.engine.listener;

import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.notification.engine.listener.BaseListener;
import com.example.mirai.projectname.notificationservice.engine.processor.review.Statuses;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.LinkedHashMap;
import java.util.Objects;

@Component
@Slf4j
public class ReleasePackage extends BaseListener {
    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.releasepackage",
            selector = "entity='com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage' and type='SUBMIT_AGGREGATE'")
    public void processReleasePackageSubmitted(final Message message) {
        try {
            processMessage(message, "ReleasePackageSubmitted");
            message.acknowledge();
        } catch (Exception e) {
            throw new InternalAssertionException("ReleasePackageSubmitted Message Template Processing failed");
        }
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.releasepackage",
            selector = "entity='com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage' and type in('READY','REREADY')")
    public void processReleasePackageReadyForRelease(final Message message) {
        try {
            processMessage(message, "ReleasePackageReadyForRelease");
            message.acknowledge();
        } catch (Exception e) {
            throw new InternalAssertionException("ReleasePackageReadyForRelease Message Template Processing failed");
        }
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.releasepackage",
            selector = "entity='com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage' and type='OBSOLETE'")
    public void processReleasePackageObsoleted(final Message message) {
        try {
            processMessage(message, "ReleasePackageObsoleted");
            message.acknowledge();
        } catch (Exception e) {
            throw new InternalAssertionException("ReleasePackageObsoleted Message Template Processing failed");
        }
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.releasepackage",
            selector = "entity='com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage' and type in('CREATE','RECREATE')")
    public void processReleasePackageCreated(final Message message) throws JMSException {
        try {
            processMessage(message, "ReleasePackageCreated");
            message.acknowledge();
        } catch (Exception e) {
            throw new InternalAssertionException("ReleasePackageCreated Message Template Processing failed");
        }
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.releasepackage",
            selector="entity='com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage' and type='RELEASE'")
    public void processReleasePackageReleased(final Message message) {
        try {
            processMessage(message, "ReleasePackageReleased");
            message.acknowledge();
        } catch (Exception e) {
            throw new InternalAssertionException("ReleasePackageReleased Message Template Processing failed");
        }
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.releasepackage",
            selector="entity='com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage' and type='CLOSE'")
    public void processReleasePackageClosed(final Message message) {
        try {
            processMessage(message, "ReleasePackageClosed");
            message.acknowledge();
        } catch (Exception e) {
            throw new InternalAssertionException("ReleasePackageClosed Message Template Processing failed");
        }
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.releasepackage",
            selector = "entity='com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage' and type='REVIEWUPDATE'")
    public void processReleasePackageReviewValidationStarted(final Message message) {
        try {
            String jsonString = message.getBody(String.class);
            String reviewStatus = getReviewStatus(jsonString);
            if (Objects.nonNull(reviewStatus) && Objects.equals(Integer.parseInt(reviewStatus), Statuses.ReviewStatus.VALIDATIONSTARTED.getStatusCode()))
                processMessage(message, "ReleasePackageReviewValidationStarted");
            else if (Objects.nonNull(reviewStatus) &&  Objects.equals(Integer.parseInt(reviewStatus), Statuses.ReviewStatus.OPENED.getStatusCode()))
                processMessage(message, "ReleasePackageReviewCreated");
            else if (Objects.nonNull(reviewStatus) && Objects.equals(Integer.parseInt(reviewStatus), Statuses.ReviewStatus.COMPLETED.getStatusCode()))
                processMessage(message, "ReleasePackageReviewCompleted");
            message.acknowledge();
        } catch (Exception e) {
            throw new InternalAssertionException("ReleasePackageReviewStatusUpdate Message Template Processing failed");
        }
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.releasepackage",
            selector = "entity='com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto.AutomaticClosureError' and type='AUTOMATIC-CLOSURE'")
    public void processReleasePackageAutomaticClosure(final Message message) throws JMSException {

        try {
            processMessage(message, "ReleasePackageAutomaticClosure");
            message.acknowledge();
        } catch (Exception e) {
            throw new InternalAssertionException("ReleasePackageAutomaticClosure Message Template Processing failed");
        }
    }

    private String getReviewStatus(String jsonString) {
        String reviewStatus = null;
        JSONArray jsonArray = (JSONArray) ((LinkedHashMap) ((LinkedHashMap) JsonPath.parse(jsonString).read("data")).get("release_package")).get("contexts");
        LinkedHashMap contextList = null;
        for (int i = 0; i < jsonArray.size(); i++) {
            contextList = (LinkedHashMap) jsonArray.get(i);
            if (contextList.get("type").equals("REVIEW")) {
                reviewStatus = contextList.get("status").toString();
            }
        }
        return reviewStatus;
    }
}
