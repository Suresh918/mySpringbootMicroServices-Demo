package com.example.mirai.projectname.notificationservice.engine.processor.shared.role;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.projectname.notificationservice.engine.config.MychangeLinkConfigurationProperties;

public class MychangeBaseRole extends BaseRole {

    public MychangeBaseRole(Event event, String role, String category, Long entityId, Long id) {
        super(event, role, category, entityId, id);
    }

    @Override
    public String getTitle() {
        return null;
    }

    protected MychangeLinkConfigurationProperties getMychangeLinkConfigurationProperties() {
        return (MychangeLinkConfigurationProperties) ApplicationContextHolder.getBean(MychangeLinkConfigurationProperties.class);
    }

    public String getNotificationsUrl() {
        String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
        String notificationsUrl = getMychangeLinkConfigurationProperties().getMychange().getNotifications();
        return baseUrl + notificationsUrl;
    }
}
