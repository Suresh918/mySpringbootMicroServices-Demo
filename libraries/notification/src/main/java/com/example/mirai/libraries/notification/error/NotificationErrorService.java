package com.example.mirai.libraries.notification.error;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.notification.error.model.IrrecoverableNotificationException;
import com.example.mirai.libraries.notification.error.model.UserAwareException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationErrorService {
    private NotificationErrorRepository notificationErrorRepository;
    private ObjectMapper objectMapper;

    public void recordException(Message message, Exception exceptionToRecord, String description) {
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            NotificationError notificationError = new NotificationError();

            String type = null;
            String payload = null;
            try {
                type = message.getStringProperty("type");
                payload = message.getStringProperty("entity");

                notificationError.setMessageType(type);
                notificationError.setMessageEntity(payload);
            } catch(Exception exception) {
                log.warn("Unable to extract type or payload while recording notification error");
            }

            notificationError.setDescription(description);

            notificationError.setMessage(objectMapper.writeValueAsString(message));
            notificationError.setTimestamp(new Date());
            if (exceptionToRecord instanceof IrrecoverableNotificationException) {
                notificationError.setEvent(((IrrecoverableNotificationException) exceptionToRecord).getEvent());
                ((IrrecoverableNotificationException) exceptionToRecord).getException().printStackTrace(printWriter);
                notificationError.setException(stringWriter.toString());
            } else {
                exceptionToRecord.printStackTrace(printWriter);
                notificationError.setException(stringWriter.toString());
            }

            if (exceptionToRecord instanceof UserAwareException) {
                notificationError.setUserId(((UserAwareException) exceptionToRecord).getUserId());
            }
            addError(notificationError);
        } catch (Exception exception) {
            log.error("Unable to record error in database");
            exception.printStackTrace();
        }

    }

    public void recordException(Event event, Exception exceptionToRecord, String description) {
        try {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            NotificationError notificationError = new NotificationError();

            String type = null;
            String payload = null;
            try {
                type = event.getType();
                payload = event.getEntity();

                notificationError.setMessageType(type);
                notificationError.setMessageEntity(payload);
            } catch (Exception exception) {
                log.warn("Unable to extract type or payload while recording notification error");
            }

            notificationError.setDescription(description);
            notificationError.setTimestamp(new Date());
            if (exceptionToRecord instanceof IrrecoverableNotificationException) {
                notificationError.setEvent(((IrrecoverableNotificationException) exceptionToRecord).getEvent());
                ((IrrecoverableNotificationException) exceptionToRecord).getException().printStackTrace(printWriter);
                notificationError.setException(stringWriter.toString());
            } else {
                exceptionToRecord.printStackTrace(printWriter);
                notificationError.setException(stringWriter.toString());
            }

            if (exceptionToRecord instanceof UserAwareException) {
                notificationError.setUserId(((UserAwareException) exceptionToRecord).getUserId());
            }
            addError(notificationError);
        } catch (Exception exception) {
            log.error("Unable to record error in database");
            exception.printStackTrace();
        }

    }


    public void addError(NotificationError notificationError) {
        notificationErrorRepository.save(notificationError);
    }

}
