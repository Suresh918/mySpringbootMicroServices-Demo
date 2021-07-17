package com.example.mirai.projectname.notificationservice.engine.listener;

import com.example.mirai.libraries.notification.engine.listener.BaseListener;
import com.example.mirai.libraries.notification.error.NotificationErrorService;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;


@Component
public class Review extends BaseListener {

    public Review(NotificationErrorService notificationErrorService) {
        super(notificationErrorService);
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.review",
            selector = "entity='com.example.mirai.projectname.reviewservice.review.model.Review' and type='CREATE-AGGREGATE'")
    public void processReviewCreated(final Message message) throws JMSException {
        processMessage(message, "ReviewCreated");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.reviewtask",
            selector = "entity='com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask' and type='MERGE'")
    public void processReviewTaskUpdated(final Message message) throws JMSException {
        processMessage(message, "ReviewTaskUpdated");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.review",
            selector = "entity='com.example.mirai.projectname.reviewservice.review.model.Review' and type='STARTVALIDATION'")
    public void processReviewValidationStarted(final Message message) throws JMSException {
        processMessage(message, "ReviewValidationStarted");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.review",
            selector = "entity='com.example.mirai.projectname.reviewservice.review.model.Review' and type='COMPLETE'")
    public void processReviewCompleted(final Message message) throws JMSException {
        processMessage(message, "ReviewCompleted");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.reviewtask",
            selector = "entity='com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask' and type='CREATE'")
    public void processReviewTaskCreated(final Message message) throws JMSException {
        processMessage(message, "ReviewTaskCreated");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.reviewentry",
            selector = "entity='com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry' and type='CREATE'")
    public void processReviewEntryCreated(final Message message) throws JMSException {
        processMessage(message, "ReviewEntryCreated");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.reviewtask",
            selector = "entity='com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask' and type='DELETE'")
    public void processReviewTaskDeleted(final Message message) throws JMSException {
        processMessage(message, "ReviewTaskDeleted");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.reviewentry",
            selector = "entity='com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry' and type='UPDATE'")
    public void processReviewEntryUpdated(final Message message) throws JMSException {
        processMessage(message, "ReviewEntryUpdated");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.reviewtask",
            selector = "entity='com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask' and type='DUE-DATE-SOON'")
    public void processReviewTaskDueDateSoon(final Message message) throws JMSException {
        processMessage(message, "ReviewTaskDueDateSoon");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.reviewtask",
            selector = "entity='com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask' and type='DUE-DATE-EXPIRED'")
    public void processReviewTaskDueDateExpired(final Message message) throws JMSException {
        processMessage(message, "ReviewTaskDueDateExpired");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.reviewtask",
            selector = "entity='com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask' and type='COMPLETE'")
    public void processReviewTaskCompleted(final Message message) throws JMSException {
        processMessage(message, "ReviewTaskCompleted");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.reviewentry",
            selector = "entity='com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry' and type='COMPLETE'")
    public void processReviewEntryCompleted(final Message message) throws JMSException {
        processMessage(message, "ReviewEntryCompleted");
    }
}
