package com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.ReleasePackageRole;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.model.AutomaticClosureJson;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class OperationRole extends ReleasePackageRole {
    public AutomaticClosureJson automaticClosureJson;

    public OperationRole(Event event, String role, User user, String category, Long entityId, Long id) {
        super(event, role, category, entityId, id);
        this.recipient = user;
        this.automaticClosureJson = getAutomaticClosureDetails();
    }

    @SneakyThrows
    public AutomaticClosureJson getAutomaticClosureDetails() {
        Map data = (Map) event.getData();
        AutomaticClosureJson releasePackageJson = new AutomaticClosureJson(data);
        return releasePackageJson;
    }


    public String getReleasePackageNumber() {
        Map data = (Map) event.getData();
        return automaticClosureJson.getReleasePackageNumberForEmailSubject(data);
    }

    public String getEcnId() {
        Map data = (Map) event.getData();
        return (automaticClosureJson.getEcnIdForEmailSubject(data));
    }

    public List<String> getAutomaticClosureErrors() {
        Map data = (Map) event.getData();
        return automaticClosureJson.getAutomaticClosureErrorValues(data);
    }
}
