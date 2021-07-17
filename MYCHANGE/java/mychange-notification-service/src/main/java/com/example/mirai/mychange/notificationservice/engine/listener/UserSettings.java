package com.example.mirai.projectname.notificationservice.engine.listener;


import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.listener.BaseListener;
import com.example.mirai.libraries.notification.error.NotificationErrorService;
import com.example.mirai.libraries.notification.settings.model.Settings;
import com.example.mirai.libraries.notification.settings.model.Subscription;
import com.example.mirai.libraries.notification.settings.repository.SettingsRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;


@Component
public class UserSettings extends BaseListener {

    @Autowired
    SettingsRepository settingsRepository;

    public UserSettings(NotificationErrorService notificationErrorService) {
        super(notificationErrorService);
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.notification",
            selector = "entity='com.example.mirai.services.userservice.profile.Profile' and type='PROFILE-CREATED'")
    public void processProfileCreated(final Message message) throws JMSException, IOException, URISyntaxException {
        Settings settings = new Settings();
        //String userId = JsonPath.parse(((TibjmsTextMessage) message).getText()).read("$.user_id");
        Event event = convertMessageToEvent(message);
         User user =event.getActor();
         if(user.getUserId()!=null && !user.getUserId().equals("")) {
            Set<Subscription> subscriptions = getDefaultSubscriptions();
            settings.setUserId(user.getUserId());
            settings.setSubscriptions(subscriptions);
            settingsRepository.save(settings);
        }
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.lastloggedinreport",
            selector = "entity='com.example.mirai.services.userservice.profile.Profile' and type='LAST-LOGGED-IN-REPORT'")
    public void processLastLoggedInUsersReport(final Message message) throws JMSException {
        processMessage(message, "LastLoggedInReport");

    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.user-settings",
            selector = "entity='com.example.mirai.projectname.notificationservice.settings.model.Settings' and type='DELEGATE-ADDED'")
    public void processDelegateAdded(final Message message) throws JMSException {
        processMessage(message, "DelegateAdded");
    }

    @JmsListener(destination = "com.example.mirai.projectname.notificationservice.user-settings",
            selector = "entity='com.example.mirai.projectname.notificationservice.settings.model.Settings' and type='DELEGATE-REMOVED'")
    public void processDelegateRemoved(final Message message) throws JMSException {
        processMessage(message, "DelegateRemoved");
    }

   public Set<Subscription>  getDefaultSubscriptions() throws IOException, URISyntaxException {
       URL url = this.getClass().getResource("/settings");
       File parentDirectory = new File(new URI(url.toString()));
       ObjectMapper mapper = new ObjectMapper();
       Set<Subscription> subscriptionSet =  mapper.readValue(new File(parentDirectory, "default-subscriptions.json"), new TypeReference<Set<Subscription>>() { });
       return subscriptionSet;
    }

}
