package com.example.mirai.projectname.notificationservice.engine.processor.myteam.role;

import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.changerequest.ChangeRequestStatus;
import com.example.mirai.projectname.notificationservice.engine.processor.changerequest.PriorityValues;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.ReleasePackageStatus;
import com.example.mirai.projectname.notificationservice.engine.processor.shared.role.MychangeBaseRole;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class MyTeamMemberRole extends MychangeBaseRole {
    private String caseType;

    public MyTeamMemberRole(Event event, String role, User user, String category, Long id, Long entityId) {
        super(event, role, category, entityId, id);
        setCaseType(event.getData());
        this.recipient = user;
    }

    @Override
    public String getTitle() {
        Long entityId = getEntityId();
        switch (category) {
            case "ChangeRequestMyTeamMemberAdded":
                return "Added to myTeam of CR " + entityId;
            case "ReleasePackageMyTeamMemberAdded":
                return "Added to myTeam of RP " + getReleasePackageNumber();
            case "ChangeRequestMyTeamMemberRemoved":
                return "Removed from myTeam of CR " + entityId;
            case "ReleasePackageMyTeamMemberRemoved":
                return "Removed from myTeam of RP " + getReleasePackageNumber();
            default:
                return null;
        }
    }

    public void setCaseType(Object data) {
        this.caseType = (String) ((LinkedHashMap) JsonPath.parse(data).read("$.related_entity")).keySet().iterator().next();
    }

    public boolean isChangeRequestMember() {
        return this.caseType.equals("change_request");
    }

    public boolean isReleasePackageMember() {
        return this.caseType.equals("release_package");
    }

    public Long getEntityId() {
        if (isChangeRequestMember()) {
            return Long.parseLong(JsonPath.parse(getEvent().getData()).read("$.related_entity." + this.caseType + ".description.id").toString());
        } else if (isReleasePackageMember()) {
            return Long.parseLong(JsonPath.parse(getEvent().getData()).read("$.related_entity." + this.caseType + ".release_package.id").toString());
        }
        return null;
    }

    public String getReleasePackageNumber () {
        return JsonPath.parse(getEvent().getData()).read("$.related_entity." + this.caseType + ".release_package.release_package_number");
    }
    public String getStatus() {
        Integer status = getStatusCode();
        if (isReleasePackageMember()) {
            return ReleasePackageStatus.getLabelByCode(status);
        } else if (isChangeRequestMember()) {
            return ChangeRequestStatus.getLabelByCode(status);
        }
        return null;
    }

    public Integer getStatusCode() {
        Map data = (Map) getEvent().getData();
        if (isReleasePackageMember()) {
            return JsonPath.parse(data).read("$.related_entity." + caseType + ".release_package.status");
        } else if (isChangeRequestMember()) {
            return JsonPath.parse(data).read("$.related_entity." + caseType + ".description.status");
        }
        return null;
    }

    public String getPriority() {
        Map data = (Map) getEvent().getData();
        Integer[] statuses = new Integer[]{1, 2, 3, 4};
        Integer priority = null;
        if (Arrays.asList(statuses).contains(getStatusCode())) {
            priority = JsonPath.parse(data).read("$.related_entity." + caseType + ".description.analysis_priority");
        } else {
            priority = JsonPath.parse(data).read("$.related_entity." + caseType + ".description.implementation_priority");
        }
        return Objects.isNull(priority) ? "-" : PriorityValues.getPriorityByCode(priority);
    }

    public String getChangeRequestDetailsLink() {
        String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
        String changeRequestDetailsUrl = getMychangeLinkConfigurationProperties().getMychange().getChangeRequest();
        return baseUrl + changeRequestDetailsUrl.replace("{ID}", getEntityId().toString());
    }

    public String getReleasePackageDetailsLink() {
        String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
        String releasePackageDetailsUrl = getMychangeLinkConfigurationProperties().getMychange().getReleasePackage();
        return baseUrl + releasePackageDetailsUrl.replace("{ID}", getReleasePackageNumber());
    }

    public User getChangeSpecialist1() {
        Map data = (Map) getEvent().getData();
        Map user = JsonPath.parse(data).read("$.related_entity." + caseType + ".description.change_specialist1");
        if (Objects.nonNull(user)) {
            return getObjectMapper().convertValue(user, User.class);
        }
        return null;
    }

    public User getChangeSpecialist2() {
        Map data = (Map) getEvent().getData();
        JSONArray user = JsonPath.parse(data).read("$.related_entity." + caseType + ".my_team_details.members[?('changeSpecialist2' in @.member.roles)]");
        if (Objects.nonNull(user) && !user.isEmpty()) {
            return getObjectMapper().convertValue(JsonPath.parse(user).read("$.[0].member.user"), User.class);
        }
        return null;
    }

    public User getChangeSpecialist3() {
        Map data = (Map) getEvent().getData();
        Map user = JsonPath.parse(data).read("$.related_entity." + caseType + ".release_package.change_specialist3");
        if (Objects.nonNull(user)) {
            return getObjectMapper().convertValue(user, User.class);
        }
        return null;
    }

    public User getExecutor() {
        Map data = (Map) getEvent().getData();
        Map user = JsonPath.parse(data).read("$.related_entity." + caseType + ".release_package.executor");
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

    public String getChangeSpecialist3Name() {
        User changeSpecialist3 = getChangeSpecialist3();
        if (Objects.isNull(changeSpecialist3) || Objects.isNull(changeSpecialist3.getUserId())) {
            return "-";
        }
        return formatUserName(changeSpecialist3);
    }

    public String getChangeSpecialist3PhotoUrl() {
        User changeSpecialist3 = getChangeSpecialist3();
        if (Objects.isNull(changeSpecialist3) || Objects.isNull(changeSpecialist3.getUserId())) {
            return "";
        }
        return getPhotoUrl(changeSpecialist3.getUserId());
    }
    public String getExecutorName() {
        User changeSpecialist1 = getExecutor();
        if (Objects.isNull(changeSpecialist1) || Objects.isNull(changeSpecialist1.getUserId())) {
            return "-";
        }
        return formatUserName(changeSpecialist1);
    }

    public String getExecutorPhotoUrl() {
        User changeSpecialist1 = getExecutor();
        if (Objects.isNull(changeSpecialist1) || Objects.isNull(changeSpecialist1.getUserId())) {
            return "";
        }
        return getPhotoUrl(changeSpecialist1.getUserId());
    }
    public String getCreatedOn() {
        Map data = (Map) getEvent().getData();
        String createdOn = null;
        if (isChangeRequestMember()) {
            createdOn = JsonPath.parse(data).read("$.related_entity." + caseType + ".description.created_on");
        } else if (isReleasePackageMember()) {
            createdOn = JsonPath.parse(data).read("$.related_entity." + caseType + ".release_package.created_on");
        } else {
            return createdOn;
        }
        return formatDate(createdOn);
    }

    public String getDiabomLink() {
        if (isChangeRequestMember()) {
            String diaLink = getMychangeLinkConfigurationProperties().getDiaByChangeRequestId();
            return diaLink.replace("{ID}", getEntityId().toString());
        } else if(isReleasePackageMember()) {
            String changeNoticeId = getChangeNoticeId();
            String diaLink = getMychangeLinkConfigurationProperties().getDiaByChangeNoticeId();
            return diaLink.replace("{ID}", changeNoticeId);
        }
        return null;
    }

    private String getChangeNoticeId() {
        Map data = (Map) getEvent().getData();
        JSONArray changeNoticeContext =JsonPath.parse(data).read("$.related_entity." + caseType + ".release_package.contexts[?(@.type=='CHANGENOTICE')].context_id");
        if (Objects.nonNull(changeNoticeContext) && !changeNoticeContext.isEmpty()) {
            return changeNoticeContext.get(0).toString();
        }
        return null;
    }

    public String getImsLink() {
        String imsLink = getMychangeLinkConfigurationProperties().getMychange().getIms();
        String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
        return baseUrl + imsLink.replace("{ID}", getEntityId().toString());
    }

    public String getEcnLink() {
        String teamCenterId = getTeamCenterId();
        if (Objects.isNull(teamCenterId))
            teamCenterId = "";
        return getMychangeLinkConfigurationProperties().getEcn().replace("{TEAMCENTER-ID}", teamCenterId);
    }

    public String getDelta2Link() {
        String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
        String delta2Url = getMychangeLinkConfigurationProperties().getDelta2();
        return delta2Url.replace("{RELEASE-PACKAGE-NUMBER}", getReleasePackageNumber()).
                replace("{SOURCE-SYSTEM-ALIAS-ID}",getReleasePackageECNId());
    }

    private String getReleasePackageECNId() {
        Map data = (Map) getEvent().getData();
        JSONArray ecnContext =JsonPath.parse(data).read("$.related_entity." + caseType + ".release_package.contexts[?(@.type=='ECN')].context_id");
        if (Objects.nonNull(ecnContext) && !ecnContext.isEmpty())
            return ecnContext.get(0).toString();
        return null;
    }

    public String getECNLink() {
        String ecnReviewLink = getMychangeLinkConfigurationProperties().getEcn();
        String tcId = getTeamCenterId();
        return  Objects.nonNull(tcId) ? ecnReviewLink.replace("{TEAMCENTER-ID}", tcId) : ecnReviewLink.replace("{TEAMCENTER-ID}", "") ;
    }

    private String getTeamCenterId() {
        Map data = (Map) getEvent().getData();
        JSONArray teamCenterContext =JsonPath.parse(data).read("$.related_entity." + caseType + ".release_package.contexts[?(@.type=='TEAMCENTER')].context_id");
        if (Objects.nonNull(teamCenterContext) && !teamCenterContext.isEmpty())
            return teamCenterContext.get(0).toString();
        return null;
    }

    public String getDelta1Link() {
        String delta1Link = getMychangeLinkConfigurationProperties().getDelta1();
        String tcId = getTeamCenterId();
        return Objects.nonNull(tcId) ? delta1Link.replace("{DELTA-REPORT-ID}", tcId) : delta1Link.replace("{DELTA-REPORT-ID}", "");
    }
}
