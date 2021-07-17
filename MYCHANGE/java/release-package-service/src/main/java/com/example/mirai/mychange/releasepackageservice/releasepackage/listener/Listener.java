package com.example.mirai.projectname.releasepackageservice.releasepackage.listener;

import javax.jms.JMSException;
import javax.jms.Message;

import com.example.mirai.libraries.deltareport.service.DeltaReportService;
import com.example.mirai.projectname.libraries.bpm.BPMEvent;
import com.example.mirai.projectname.libraries.bpm.BWEvent;
import com.example.mirai.projectname.libraries.model.ChangeObject;
import com.example.mirai.projectname.libraries.model.ChangeRequest;
import com.example.mirai.projectname.libraries.model.ReviewAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageSynchronizationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class Listener {

    private ReleasePackageSynchronizationService releasePackageSynchronizationService;
    private DeltaReportService deltaReportService;

    @JmsListener(destination = "com.example.mirai.projectname.releasepackageservice.review")
    public void processReviewUpdated(final Message message) throws JMSException {
        ReviewAggregate reviewAggregate = new ReviewAggregate(message.getBody(String.class));
        releasePackageSynchronizationService.updateReleasePackageLinkedToReview(reviewAggregate);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.releasepackageservice.bpmevent.changenotice")
    public void processChangeNoticeUpdated(final Message message) throws JMSException {
        BPMEvent bpmEvent = convertMessageToBPMEvent(message);
        releasePackageSynchronizationService.updateReleasePackageLinkedToChangeNotice(bpmEvent);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.releasepackageservice.bwevent.changenotice")
    @SneakyThrows
    public void processChangeNoticeReconciliation(final Message message) {
        BWEvent bwEvent = convertMessageToBWEvent(message);
        releasePackageSynchronizationService.updateReleasePackageLinkedToChangeNotice(bwEvent);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.releasepackageservice.bpmevent.action")
    public void processActionUpdated(final Message message) throws JMSException {
        BPMEvent bpmEvent = convertMessageToBPMEvent(message);
        releasePackageSynchronizationService.updateReleasePackageLinkedToAction(bpmEvent);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.releasepackageservice.bwevent.action")
    @SneakyThrows
    public void processActionReconciliation(final Message message) {
        BWEvent bwEvent = convertMessageToBWEvent(message);
        releasePackageSynchronizationService.updateReleasePackageLinkedToAction(bwEvent);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.releasepackageservice.changerequest")
    public void processChangeRequestUpdated(final Message message) throws JMSException {
        ChangeRequest changeRequest = new ChangeRequest(message.getBody(String.class), "description", "RELEASEPACKAGE");
        releasePackageSynchronizationService.updateReleasePackageLinkedToChangeRequest(changeRequest);
        message.acknowledge();
    }


    @JmsListener(destination = "com.example.mirai.projectname.releasepackageservice.changeobject",
            selector = "entity='com.example.mirai.projectname.impacteditemservice.changeobject.model.aggregate.ChangeObjectAggregate' and type='CREATE_AGGREGATE'")
    public void processChangeObjectCreated(final Message message) throws JMSException  {
        ChangeObject changeObject = new ChangeObject(message.getBody(String.class));
        releasePackageSynchronizationService.updateContextWithChangeObject(changeObject);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.releasepackageservice.changeobject",
            selector = "entity='com.example.mirai.projectname.impacteditemservice.changeobject.model.aggregate.ChangeObjectAggregate' and type='STATUS_UPDATE'")
    public void processChangeObjectStatusUpdated(final Message message) throws JMSException  {
        ChangeObject changeObject = new ChangeObject(message.getBody(String.class));
        releasePackageSynchronizationService.updateContextWithChangeObject(changeObject);
        message.acknowledge();
    }


    @JmsListener(destination = "com.example.mirai.projectname.releasepackageservice.deltareport")
    public void processDeltaReport(final Message message) throws JMSException, JsonProcessingException {
        deltaReportService.processDeltaReport(message);
        message.acknowledge();
    }

    @JmsListener(destination = "com.example.mirai.projectname.releasepackageservice.changeobject",
            selector = "entity='com.example.mirai.projectname.impacteditemservice.changeobject.model.aggregate.ChangeObjectAggregate' and type in ('RECONCILIATION', 'MOVE_SCOPE_ITEM')")
    @SneakyThrows
    public void processChangeObjectReconciliation(final Message message) {
        ChangeObject changeObject = new ChangeObject(message.getBody(String.class));
        releasePackageSynchronizationService.updateReleasePackageLinkedToChangeObject(changeObject);
        message.acknowledge();
    }

    private BPMEvent convertMessageToBPMEvent(Message message) {
        try {
            String xmlString = message.getBody(String.class);
            return new BPMEvent(xmlString);
        } catch (JMSException exception) {
			log.error("JMS exception occurred", exception);
        }
        return null;

    }

    private BWEvent convertMessageToBWEvent(Message message) {
        try {
            String xmlString = message.getBody(String.class);
            return new BWEvent(xmlString);
        } catch (JMSException exception) {
			log.error("JMS exception occurred", exception);
        }
        return null;
    }
}
