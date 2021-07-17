package com.example.mirai.projectname.notificationservice.engine.processor.myteam.extractor;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.example.mirai.projectname.notificationservice.engine.processor.myteam.role.MyTeamMemberBulkUpdateActorRole;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MyTeamMemberBulkRoleExtractor implements RoleExtractorInterface {
    public User addedMyTeamMember;
    public User removedMyTeamMember;
    public String role;
    public String category;

    @Override
    public Set<BaseRole> getProcessors(Event event) throws IOException {
        Set<BaseRole> baseRoles = new HashSet();
        Map data = (Map) event.getData();
        if (Objects.nonNull(JsonPath.parse(data).read("$.user_to_add")))
            this.addedMyTeamMember = getObjectMapper().convertValue(JsonPath.parse(data).read("$.user_to_add"), User.class);
        if (Objects.nonNull(JsonPath.parse(data).read("$.user_to_remove")))
            this.removedMyTeamMember = getObjectMapper().convertValue(JsonPath.parse(data).read("$.user_to_remove"), User.class);

        if (Objects.nonNull(event.getActor())) {
            if (event.getStatus().equals("SUCCESS"))
                baseRoles.add(new MyTeamMemberBulkUpdateActorRole(event, "Actor", event.getActor(), category, null, null));
            else if (event.getStatus().equals("ERROR"))
                baseRoles.add(new MyTeamMemberBulkUpdateActorRole(event, "ActorError", event.getActor(), category, null, null));
        }
        return baseRoles;
    }

    @Override
    public Boolean isStatusChangeEvent() {
        return false;
    }
}
