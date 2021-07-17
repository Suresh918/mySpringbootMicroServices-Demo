package com.example.mirai.projectname.notificationservice.engine.processor.myteam.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.shared.role.MychangeBaseRole;
import com.jayway.jsonpath.JsonPath;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyTeamMemberBulkUpdateActorRole extends MychangeBaseRole {

    public MyTeamMemberBulkUpdateActorRole(Event event, String role, User user, String category, Long id, Long entityId) {
        super(event, role, category, entityId, id);
        this.recipient = user;
    }

    @Override
    public boolean ignoreRecipientCheck() {
        return true;
    }

    @Override
    public boolean ignoreSubscriptionCheck() {
        return true;
    }

    @Override
    public String getTitle() {
        Long entityId = getEntityId();

        switch (category) {
            case "ChangeRequestMyTeamMemberAdded":
                return "Added to My Team of CR " + entityId;
            case "ReleasePackageMyTeamMemberAdded":
                return "Added to My Team of RP " + entityId;
            case "ChangeRequestMyTeamMemberRemoved":
                return "Removed from My Team of CR " + entityId;
            case "ReleasePackageMyTeamMemberRemoved":
                return "Removed from My Team of RP " + entityId;
            default:
                return null;
        }
    }

    public String getAction() {
        if (category.equals("ChangeRequestMyTeamMemberBulkAdded") || category.equals("ReleasePackageMyTeamMemberBulkAdded")) {
            if (this.getRole().equals("ActorError"))
                return "Adding";
            return "Added";
        }
        if (category.equals("ChangeRequestMyTeamMemberBulkRemoved") || category.equals("ReleasePackageMyTeamMemberBulkRemoved")) {
            if (this.getRole().equals("ActorError"))
                return "Removing";
            return "Removed";
        }
        if (category.equals("ChangeRequestMyTeamMemberBulkReplaced") || category.equals("ReleasePackageMyTeamMemberBulkReplaced")) {
            if (this.getRole().equals("ActorError"))
                return "Replacing";
            return "Replaced";
        }
        return "Updated";
    }

    public User getAddedUser() {
        if (Objects.nonNull(JsonPath.parse(getEvent().getData()).read("$.user_to_add")))
            return getObjectMapper().convertValue(JsonPath.parse(getEvent().getData()).read("$.user_to_add"), User.class);
        return null;
    }

    public User getRemovedUser() {
        if (Objects.nonNull(JsonPath.parse(getEvent().getData()).read("$.user_to_remove")))
            return getObjectMapper().convertValue(JsonPath.parse(getEvent().getData()).read("$.user_to_remove"), User.class);
        return null;
    }

    public String getActionUserName() {
        if (getAction().equals("Added") || getAction().equals("Adding")) {
            return Objects.nonNull(getAddedUser()) ? formatUserName(getAddedUser()) : "";
        }
        if (getAction().equals("Removed") || getAction().equals("Replaced") || getAction().equals("Removing") || getAction().equals("Replacing")) {
            return Objects.nonNull(getRemovedUser()) ? formatUserName(getRemovedUser()) : "";
        }
        return "";
    }

    public String getTargetUserName() {
        return Objects.nonNull(getAddedUser()) ? formatUserName(getAddedUser()) : "";
    }

    public String getTargetUserPhotoUrl() {
        return Objects.nonNull(getAddedUser()) ? getPhotoUrl(getAddedUser().getUserId()) : "";
    }

    public String getActionUserPhotoUrl() {
        if (getAction().equals("Added")) {
            return Objects.nonNull(getAddedUser()) ? getPhotoUrl(getAddedUser().getUserId()) : "";
        }
        if (getAction().equals("Removed") || getAction().equals("Replaced")) {
            return Objects.nonNull(getRemovedUser()) ? getPhotoUrl(getRemovedUser().getUserId()) : "";
        }
        return "";
    }

    public List getCaseObjectNumbers() {
        return getObjectMapper().convertValue(JsonPath.parse(getEvent().getData()).read("$.case_object_numbers"), List.class);
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

    public String getActionUserAbbreviation() {
        if (getAction().equals("Added")) {
            return Objects.nonNull(getAddedUser()) ? getAddedUser().getAbbreviation() : "";
        }
        if (getAction().equals("Removed") || getAction().equals("Replaced")) {
            return Objects.nonNull(getRemovedUser()) ? getRemovedUser().getAbbreviation() : "";
        }
        return "";
    }

    public String getUpdatedOn() {
        return formatDate(new Timestamp(getEvent().getTimestamp()).toInstant().toString());
    }
}
