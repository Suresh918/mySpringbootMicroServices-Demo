package com.example.mirai.projectname.notificationservice.engine.processor.myteam.extractor;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.projectname.notificationservice.engine.processor.myteam.role.MyTeamMemberBulkUpdateRole;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component("ReleasePackageMyTeamMemberBulkRemoved")
public class ReleasePackageMyTeamMemberBulkRemovedRoleExtractor extends MyTeamMemberBulkRoleExtractor {
    public Set<BaseRole> getProcessors(Event event) throws IOException {
        this.category = "ReleasePackageMyTeamMemberBulkRemoved";
        Set<BaseRole> baseRoles = super.getProcessors(event);
        if (event.getStatus().equals("SUCCESS"))
            baseRoles.add(new MyTeamMemberBulkUpdateRole(event, "MyTeamMember", removedMyTeamMember, category, null, null));
        //baseRoles.add(new MyTeamMemberRole(event, "MyTeamMember", myTeamMemberUser, "ReleasePackageMyTeamMemberAdded", myTeamMemberId, getEntityId(event)));
        return baseRoles;
    }
}
