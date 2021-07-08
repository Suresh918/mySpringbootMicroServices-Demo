package com.example.mirai.libraries.notification.engine.listener;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.example.mirai.libraries.notification.error.NotificationErrorService;
import com.example.mirai.libraries.notification.error.model.IrrecoverableNotificationException;
import com.example.mirai.libraries.notification.error.model.MessageToEventConversionFailure;
import com.example.mirai.libraries.notification.error.model.RecoverableNotificationException;
import com.example.mirai.libraries.notification.error.model.UnableToFetchRoles;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public abstract class BaseListener {

    private NotificationErrorService notificationErrorService;

    protected void processMessage(final Message message, String eventType) throws JMSException {
        try {
            Event event = convertMessageToEvent(message);
            if(event != null && eventType!= null) {
                notifyRecipients(event, eventType);
            }
            message.acknowledge();
        } catch (IrrecoverableNotificationException irrecoverableNotificationException) {
            handleIrrecoverableException(message, irrecoverableNotificationException);
            message.acknowledge();
        } catch (RecoverableNotificationException recoverableNotificationException) {
            handleRecoverableException(recoverableNotificationException);
        }
    }
    protected void notifyRecipients(Event event, String eventType) {
        Set<BaseRole> baseRoles = getRolesToNotify(event, eventType);

        if(baseRoles != null) {
            baseRoles.forEach(role -> {
                try {
                    role.notifyRecipient();
                } catch (IrrecoverableNotificationException irrecoverableNotificationException) {
                    handleIrrecoverableException(event, irrecoverableNotificationException);
                } catch (RecoverableNotificationException recoverableNotificationException) {
                    handleRecoverableException(recoverableNotificationException);
                }


            });
        }
    }

    protected Set<BaseRole> getRolesToNotify(Event event, String eventType) {
        Set<BaseRole> baseRoles;
        try {
            RoleExtractorInterface situationBuilder = (RoleExtractorInterface) ApplicationContextHolder.getApplicationContext().getBean(eventType);
            baseRoles = situationBuilder.getProcessors(event);
        } catch (Exception exception) {
            throw new UnableToFetchRoles(exception, "Unable to get roles to notify from event", event);
        }
        return baseRoles;
    }


    protected Event convertMessageToEvent(Message jmsMessage) {
        Event event = null;
        try {
            String jsonString = jmsMessage.getBody(String.class);
            event = getObjectMapper().readValue(jsonString, Event.class);
            return event;
        } catch (Exception exception) {
            throw new MessageToEventConversionFailure(exception, "Could not convert JMS Message to Event object", event);
        }
    }
    ObjectMapper getObjectMapper() { return ApplicationContextHolder.getApplicationContext().getBean(ObjectMapper.class);}

    protected void handleRecoverableException(RecoverableNotificationException recoverableNotificationException) {
        String description = recoverableNotificationException.getDescription();
        log.error("Message not removed form queue: " + description + " : " + recoverableNotificationException.getMessage());
    }

    protected void handleIrrecoverableException(Message message, IrrecoverableNotificationException irrecoverableNotificationException) {
        notificationErrorService = ApplicationContextHolder.getApplicationContext().getBean(NotificationErrorService.class);
        String description = irrecoverableNotificationException.getDescription();
        log.error("Message removed form queue: " + description + " : " + irrecoverableNotificationException.getMessage());
        notificationErrorService.recordException(message, irrecoverableNotificationException, description);
    }

    protected void handleIrrecoverableException(Event event, IrrecoverableNotificationException irrecoverableNotificationException) {
        notificationErrorService = ApplicationContextHolder.getApplicationContext().getBean(NotificationErrorService.class);
        String description = irrecoverableNotificationException.getDescription();
        notificationErrorService.recordException(event, irrecoverableNotificationException, description);
    }
}
