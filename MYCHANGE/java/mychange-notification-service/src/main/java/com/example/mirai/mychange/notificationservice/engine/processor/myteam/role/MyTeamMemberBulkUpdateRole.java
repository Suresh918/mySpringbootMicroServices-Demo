package com.example.mirai.projectname.notificationservice.engine.processor.myteam.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.shared.role.MychangeBaseRole;
import com.jayway.jsonpath.JsonPath;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyTeamMemberBulkUpdateRole extends MychangeBaseRole {

    public MyTeamMemberBulkUpdateRole(Event event, String role, User user, String category, Long id, Long entityId) {
        super(event, role, category, entityId, id);
        this.recipient = user;
    }
    @Override
    public boolean ignoreSubscriptionCheck() {
        return true;
    }

    @Override
    public String getTitle() {
        List caseObjectIds = getCaseObjectNumbers();
        switch (category) {
            case "ChangeRequestMyTeamMemberBulkAdded":
                return "You have been added to myTeam in " + getCaseObjectNumbersCount() + " CRs as " + getRole();
            case "ChangeRequestMyTeamMemberBulkRemoved":
                return "You have been removed from myTeam in " + getCaseObjectNumbersCount() + " CRs as " + getRole();
            case "ChangeRequestMyTeamMemberBulkReplaced":
                return "You have been replaced in myTeam in " + getCaseObjectNumbersCount() + " CRs as " + getRole();
            case "ReleasePackageMyTeamMemberBulkAdded":
                return "You have been added to myTeam in " + getCaseObjectNumbersCount() + " RPs as " + getRole();
            case "ReleasePackageMyTeamMemberBulkRemoved":
                return "You have been removed from myTeam in " + getCaseObjectNumbersCount() + " RPs as " + getRole();
            case "ReleasePackageMyTeamMemberBulkReplaced":
                return "You have been replaced in myTeam in " + getCaseObjectNumbersCount() + " RPs as " + getRole();
            default:
                return null;
        }
    }

    public List getCaseObjectNumbers() {
        return getObjectMapper().convertValue(JsonPath.parse(getEvent().getData()).read("$.case_object_numbers"), List.class);
    }

    public String getRecipientName() {
        return formatUserName(getRecipient());
    }

    public String getRecipientAbbreviation() {
        return getRecipient().getAbbreviation();
    }

    public String getActorName() {
        return formatUserName(getActor());
    }

    public String getActorPhotoUrl() {
        return Objects.nonNull(getActor()) ? getPhotoUrl(getActor().getUserId()) : "";
    }

    public String getRecipientPhotoUrl() {
        return Objects.nonNull(getRecipient()) ? getPhotoUrl(getRecipient().getUserId()) : "";
    }

    public String getCaseObjectNumbersCsv() {
        List<String> caseObjectNumbers = (List<String>) getCaseObjectNumbers().stream().map(Object::toString).collect(Collectors.toList());
        return String.join(", ", caseObjectNumbers);
    }

    public int getCaseObjectNumbersCount() {
        return getCaseObjectNumbers().size();
    }

    public String getMemberRole() {
        return JsonPath.parse(getEvent().getData()).read("$.role");
    }

    public String getUpdatedOn() {
        return formatDate(new Timestamp(getEvent().getTimestamp()).toInstant().toString());
    }
}
