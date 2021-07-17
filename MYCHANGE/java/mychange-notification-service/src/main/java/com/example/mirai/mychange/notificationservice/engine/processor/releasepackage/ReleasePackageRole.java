package com.example.mirai.projectname.notificationservice.engine.processor.releasepackage;


import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.model.ReleasePackageJson;
import com.example.mirai.projectname.notificationservice.engine.processor.shared.role.MychangeBaseRole;
import lombok.SneakyThrows;

import java.util.Map;

/*@NoArgsConstructor
@Component*/
public class ReleasePackageRole extends MychangeBaseRole {

    public ReleasePackageJson releasePackageJson;

    public ReleasePackageRole(Event event, String role, String category, Long entityId, Long id) {
        super(event, role, category, entityId, id);
        this.releasePackageJson = getReleasePackage();
    }

    @Override
    public String getTitle() {
        String releasePackageNumber = getReleasePackageNumber();
        String ecnNumber = getReleasePackageECN();
        String releasePackageStatus = getReleasePackageStatus();
        switch (category) {
            case "ReleasePackageReviewCompleted":
                return "Review Completed RP " + releasePackageNumber + " (" +ecnNumber+ ")";
            case "ReleasePackageReviewCreated":
                return "Review Started RP " + releasePackageNumber + " (" +ecnNumber+ ")";
            case "ReleasePackageReviewValidationStarted":
                return "Review Validation Started RP " + releasePackageNumber + " (" +ecnNumber+ ")";
            default:
                return "New Status RP "+ releasePackageNumber +": " + releasePackageStatus;
        }
    }

    @SneakyThrows
    public ReleasePackageJson getReleasePackage() {
        Map data = (Map) event.getData();
        String releasePackageString = getObjectMapper().writeValueAsString(data.get("release_package"));
        ReleasePackageJson releasePackageJson = new ReleasePackageJson(releasePackageString);
        return releasePackageJson;
    }

    public Integer getReleasePackageId() {
        return releasePackageJson.getReleasePackageId();
    }

    public String getReleasePackageNumber() {
        return releasePackageJson.getReleasePackageNumber() != null ? releasePackageJson.getReleasePackageNumber() : "";
    }

    public String getReleasePackageStatus() {
        return releasePackageJson.getReleasePackageStatus() != null ?
                ReleasePackageStatus.getLabelByCode(releasePackageJson.getReleasePackageStatus()) : "";
    }

    public String getReleasePackageECN() {
        return releasePackageJson.getReleasePackageECN() != null ? releasePackageJson.getReleasePackageECN() : "";
    }

    public String getTeamCenterId(){
        return releasePackageJson.getTeamCenterId() != null ? releasePackageJson.getTeamCenterId() : "";
    }

    public String getReleasePackageTitle() {
        return releasePackageJson.getReleasePackageTitle() != null ? releasePackageJson.getReleasePackageTitle() : "";
    }


    public String getReleasePackageCreatedOn() {
        return releasePackageJson.getReleasePackageCreatedOn() != null ? formatDate(releasePackageJson.getReleasePackageCreatedOn()) : "";
    }

    public String getCaseLink() {
        String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
        String releasePackageDetailsUrl = getMychangeLinkConfigurationProperties().getMychange().getReleasePackage();
        return baseUrl + releasePackageDetailsUrl.replace("{ID}", releasePackageJson.getReleasePackageNumber());
    }

    public String getNotificationsUrl() {
        String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
        String notificationsUrl = getMychangeLinkConfigurationProperties().getMychange().getNotifications();
        return baseUrl + notificationsUrl;
    }

    public String getDelta2Link() {
        String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
        String delta2Url = getMychangeLinkConfigurationProperties().getDelta2();
        return delta2Url.replace("{RELEASE-PACKAGE-NUMBER}", releasePackageJson.getReleasePackageNumber()).
                replace("{SOURCE-SYSTEM-ALIAS-ID}", getReleasePackageECN());
    }

    public String getECNLink() {
        String ecnReviewLink = getMychangeLinkConfigurationProperties().getEcn();
        return ecnReviewLink.replace("{TEAMCENTER-ID}", getReleasePackageECN());
    }

    public String getDelta1Link() {
        String delta1Link = getMychangeLinkConfigurationProperties().getDelta1();
        return delta1Link.replace("{DELTA-REPORT-ID}", getTeamCenterId());
    }

    public String getDIALink() {
        String diaLink = getMychangeLinkConfigurationProperties().getDia();
        String cnId = getReleasePackageNumber().split("-").length > 1 ? getReleasePackageNumber().split("-")[0] : "";
        return diaLink.replace("{ID}", cnId);
    }

    public String getReleasePackageExecutor() {
        User user = releasePackageJson.getReleasePackageExecutor();
        return releasePackageJson.getReleasePackageExecutor() != null ? user.getFullName() + " (" + user.getAbbreviation() + ")" : "";
    }

    public String getReleasePackageExecutorPhotoUrl() {
        return releasePackageJson.getReleasePackageExecutor() != null ?
                getPhotoUrl(releasePackageJson.getReleasePackageExecutor().getUserId()) : "";
    }

    public String getReleasePackageChangeSpecialist3() {
        User user = releasePackageJson.getReleasePackageChangeSpecialist3();
        return releasePackageJson.getReleasePackageChangeSpecialist3() != null ? user.getFullName() + " (" + user.getAbbreviation() + ")" : "";
    }

    public String getReleasePackageChangeSpecialist3PhotoUrl() {
        return releasePackageJson.getReleasePackageChangeSpecialist3() != null ?
                getPhotoUrl(releasePackageJson.getReleasePackageChangeSpecialist3().getUserId()) : "";
    }

    public String getReviewStatus() {
        // Integer status= (Integer) ((JSONArray)(JsonPath.parse(releasePackageJson).read("$.contexts[?(@.type=='REVIEW')].status"))).get(0);
        // return Statuses.ReviewStatus.getLabelByCode(status);
        return releasePackageJson.getReviewStatus();
    }


    public String getReviewTitle() {
        return releasePackageJson.getReviewTitle();
    }

    public String getReviewLink() {
        String reviewId = releasePackageJson.getReviewId();
        String baseUrl = getMychangeLinkConfigurationProperties().getMychange().getBaseUrl();
        String reviewDetailsUrl = getMychangeLinkConfigurationProperties().getMychange().getReview();
        return baseUrl + reviewDetailsUrl.replace("{ID}", reviewId);
    }

    public String getReviewExecutorPhotoUrl() {
        return releasePackageJson.getReviewExecutor() != null ?
                getPhotoUrl(releasePackageJson.getReviewExecutor().getUserId()) : "";
    }

    public String getReviewExecutor() {
        User user = releasePackageJson.getReviewExecutor();
        return releasePackageJson.getReviewExecutor() != null ? user.getFullName() + " (" + user.getAbbreviation() + ")" : "";
    }

    public String getChangeNoticeId() {
        return releasePackageJson.getChangeNoticeId() != null ? releasePackageJson.getChangeNoticeId() : "";
    }


}
