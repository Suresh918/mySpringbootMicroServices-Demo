package com.example.mirai.projectname.notificationservice.engine.processor.changerequest.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.changerequest.ChangeRequestStatus;
import com.example.mirai.projectname.notificationservice.engine.processor.changerequest.PriorityValues;
import com.example.mirai.projectname.notificationservice.engine.processor.shared.role.MychangeBaseRole;
import com.jayway.jsonpath.JsonPath;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class ChangeRequestBaseRole extends MychangeBaseRole {
    public ChangeRequestBaseRole(Event event, String role, String category, Long id, Long entityId) {
        super(event, role, category, entityId, id);
    }

    @Override
    public String getTitle() {
        Long changeRequestId = getEntityId();
        String changeRequestStatus = getStatus();
        return "New Status CR "+ changeRequestId +": " + changeRequestStatus;
    }
    public String getEntityTitle() {
        Map data = (Map) getEvent().getData();
        return JsonPath.parse(data).read("$.description.title");
    }
    public Long getEntityId() {
        Map data = (Map) getEvent().getData();
        return Long.parseLong(JsonPath.parse(data).read("$.description.id").toString());
    }

    public String getStatus() {
        Integer status = getStatusCode();
        return ChangeRequestStatus.getLabelByCode(status);
    }

    public Integer getStatusCode() {
        Map data = (Map) getEvent().getData();
        return JsonPath.parse(data).read("$.description.status");
    }

    public String getChangeRequestDetailsLink() {
        String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
        String changeRequestDetailsUrl = getMychangeLinkConfigurationProperties().getMychange().getChangeRequest();
        return baseUrl + changeRequestDetailsUrl.replace("{ID}", getEntityId().toString());
    }

    public String getPriority() {
        // if status is 1,2,3,4 then return Analysis Priority , else Implementation Priority
        Map data = (Map) getEvent().getData();
        Integer[] statuses = new Integer[]{1, 2, 3, 4};
        Integer priority = null;
        if (Arrays.asList(statuses).contains(getStatusCode())) {
            priority = JsonPath.parse(data).read("$.description.analysis_priority");
        } else {
            priority = JsonPath.parse(data).read("$.description.implementation_priority");
        }
        return Objects.isNull(priority) ? "-" : PriorityValues.getPriorityByCode(priority);
    }

    public String getCreatedOn() {
        Map data = (Map) getEvent().getData();
        String createdOn = JsonPath.parse(data).read("$.description.created_on");
        return formatDate(createdOn);
    }

    public String getDiaLink() {
        String diaLink = getMychangeLinkConfigurationProperties().getDiaByChangeRequestId();
        return diaLink.replace("{ID}", getEntityId().toString());
    }

    public String getImsLink() {
        String imsLink = getMychangeLinkConfigurationProperties().getMychange().getIms();
        String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
        return baseUrl + imsLink.replace("{ID}", getEntityId().toString());
    }

    public User getChangeSpecialist1() {
        Map data = (Map) getEvent().getData();
        Map user = JsonPath.parse(data).read("$.description.change_specialist1");
        if (Objects.nonNull(user)) {
            return getObjectMapper().convertValue(user, User.class);
        }
        return null;
    }

    public User getChangeSpecialist2() {
        Map data = (Map) getEvent().getData();
        Map user = JsonPath.parse(data).read("$.description.change_specialist2");
        if (Objects.nonNull(user)) {
            return getObjectMapper().convertValue(user, User.class);
        }
        return null;
    }

    public String getChangeSpecialist1Name() {
        User changeSpecialist1 = getChangeSpecialist1();
        if (Objects.isNull(changeSpecialist1) || Objects.isNull(changeSpecialist1.getUserId())) {
            return "-";
        }
        return formatUserName(changeSpecialist1);
    }

    public String getChangeSpecialist1PhotoUrl() {
        User changeSpecialist1 = getChangeSpecialist1();
        if (Objects.isNull(changeSpecialist1) || Objects.isNull(changeSpecialist1.getUserId())) {
            return "";
        }
        return getPhotoUrl(changeSpecialist1.getUserId());
    }

    public String getChangeSpecialist2Name() {
        User changeSpecialist2 = getChangeSpecialist2();
        if (Objects.isNull(changeSpecialist2) || Objects.isNull(changeSpecialist2.getUserId())) {
            return "-";
        }
        return formatUserName(changeSpecialist2);
    }

    public String getChangeSpecialist2PhotoUrl() {
        User changeSpecialist2 = getChangeSpecialist2();
        if (Objects.isNull(changeSpecialist2) || Objects.isNull(changeSpecialist2.getUserId())) {
            return "";
        }
        return getPhotoUrl(changeSpecialist2.getUserId());
    }

    public User getCreator() {
        Map data = (Map) getEvent().getData();
        Map user = JsonPath.parse(data).read("$.description.creator");
        if (Objects.nonNull(user)) {
            return getObjectMapper().convertValue(user, User.class);
        }
        return null;
    }

    public String getCreatorName() {
        User changeSpecialist2 = getCreator();
        if (Objects.isNull(changeSpecialist2) || Objects.isNull(changeSpecialist2.getUserId())) {
            return "";
        }
        return getPhotoUrl(changeSpecialist2.getUserId());
    }

    public String getRequirementsForImplementation() {
        Map data = (Map) getEvent().getData();
        return JsonPath.parse(data).read("$.description.requirements_for_implementation_plan");
    }
}
