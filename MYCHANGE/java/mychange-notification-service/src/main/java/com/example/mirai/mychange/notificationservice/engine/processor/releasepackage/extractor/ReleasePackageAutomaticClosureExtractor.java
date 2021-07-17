package com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.extractor;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.model.AutomaticClosureJson;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.role.OperationRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("ReleasePackageAutomaticClosure")
public class ReleasePackageAutomaticClosureExtractor implements RoleExtractorInterface {
    @Override
    public Set<BaseRole> getProcessors(Event event) throws JsonProcessingException {
        Set<BaseRole> baseRoles = new HashSet();
        Map data = (Map) event.getData();
        AutomaticClosureJson automaticClosureJson = new AutomaticClosureJson(data);
        List<String> emailIds = automaticClosureJson.getEmailIds(data);
        emailIds.stream().forEach(emailId -> {
            User user = new User();
            user.setEmail(emailId);
            baseRoles.add(new OperationRole(event, "Operation", user, "ReleasePackageAutomaticClosure", null, null));
        });
        return baseRoles;
    }
}
