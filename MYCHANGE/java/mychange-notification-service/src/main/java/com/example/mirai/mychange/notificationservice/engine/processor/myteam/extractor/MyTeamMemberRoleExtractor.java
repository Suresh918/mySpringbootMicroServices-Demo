package com.example.mirai.projectname.notificationservice.engine.processor.myteam.extractor;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import com.jayway.jsonpath.JsonPath;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MyTeamMemberRoleExtractor implements RoleExtractorInterface {
    protected User myTeamMemberUser;
    protected Long myTeamMemberId;
    protected Long entityId;
    protected String caseType;

    @Override
    public Boolean isStatusChangeEvent() {
        return false;
    }

    @Override
    public Set<BaseRole> getProcessors(Event event) throws IOException {
        Set<BaseRole> baseRoles = new HashSet();
        this.caseType = (String) ((LinkedHashMap)JsonPath.parse(event.getData()).read("$.related_entity")).keySet().iterator().next();
        Map data = (Map) event.getData();
        this.myTeamMemberUser = getObjectMapper().convertValue(JsonPath.parse(data).read("$.my_team_member.user"), User.class);
        this.myTeamMemberId = Long.parseLong(JsonPath.parse(event.getData()).read("$.my_team_member.id").toString());
        return baseRoles;
    }


    public Long getEntityId(Event event) {
        if (this.caseType.equals("change_request")) {
            return Long.parseLong(JsonPath.parse(event.getData()).read("$.related_entity." + this.caseType + ".description.id").toString());
        } else if (this.caseType.equals("release_package")) {
            return Long.parseLong(JsonPath.parse(event.getData()).read("$.related_entity." + this.caseType + ".release_package.id").toString());
        }
        return null;
    }
}
