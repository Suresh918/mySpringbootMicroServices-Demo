package com.example.mirai.projectname.reviewservice.fixtures;


import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.sapmdg.material.model.PlantSpecific;
/*import com.example.mirai.libraries.teamcenter.ecn.model.SolutionItem;
import com.example.mirai.libraries.teamcenter.ecn.model.SolutionItemDelta;
import com.example.mirai.libraries.teamcenter.ecn.model.Tpd;*/
import com.example.mirai.libraries.deltareport.model.dto.Tpd;
import com.example.mirai.libraries.deltareport.model.dto.SolutionItem;
import com.example.mirai.libraries.deltareport.model.dto.SolutionItemDelta;
import com.example.mirai.libraries.sapmdg.material.model.PlantSpecificDelta;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewContext;
import com.example.mirai.projectname.reviewservice.review.model.dto.ecn.*;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryContext;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.shared.utils.Constants;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class EntityPojoFactory {

    public static Review createReview(String dataIdentifier, String properties) {
        Review review = createReview(dataIdentifier);

        switch (properties) {
            case Constants.ALL_PROPERTIES:
                addReviewTitle(review, dataIdentifier);
                addReviewExecutor(review, dataIdentifier);
                addReviewCompletionDate(review, dataIdentifier);
                addReviewCreator(review, dataIdentifier);
                addReviewCreatedOn(review, dataIdentifier);
                return review;
            case Constants.ALL_PROPERTIES_EXCEPT_AUDIT:
                addReviewTitle(review, dataIdentifier);
                addReviewExecutor(review, dataIdentifier);
                addReviewCompletionDate(review, dataIdentifier);
                return review;
            case "ALL_PROPERTIES_EXCEPT_AUDIT_AND_TITLE":
                addReviewExecutor(review, dataIdentifier);
                addReviewCompletionDate(review, dataIdentifier);
                return review;
            case "ALL_PROPERTIES_EXCEPT_AUDIT_AND_EXECUTOR":
                addReviewTitle(review, dataIdentifier);
                addReviewCompletionDate(review, dataIdentifier);
                return review;
            case "ALL_PROPERTIES_EXCEPT_AUDIT_AND_COMPLETION_DATE":
                addReviewTitle(review, dataIdentifier);
                addReviewExecutor(review, dataIdentifier);
                return review;
            default:
                return review;
        }
    }

    private static Review createReview(String dataIdentifier) {
        Review review = new Review();

        List<ReviewContext> reviewContexts = new ArrayList<>();

        ReviewContext releasePackageReviewContext = new ReviewContext();
        releasePackageReviewContext.setType("RELEASEPACKAGE");
        releasePackageReviewContext.setName(dataIdentifier + "_releasepackage_name");
        releasePackageReviewContext.setContextId(dataIdentifier + "_releasepackage_context_id");
        releasePackageReviewContext.setStatus(dataIdentifier + "_releasepackage_status");
        reviewContexts.add(releasePackageReviewContext);

        ReviewContext ecnReviewContext = new ReviewContext();
        ecnReviewContext.setType("ECN");
        ecnReviewContext.setName(dataIdentifier + "_ecn_name");
        ecnReviewContext.setContextId(dataIdentifier + "_ecn_context_id");
        ecnReviewContext.setStatus(dataIdentifier + "_ecn_status");
        reviewContexts.add(ecnReviewContext);

        ReviewContext teamCenterReviewContext = new ReviewContext();
        teamCenterReviewContext.setType("TEAMCENTER");
        teamCenterReviewContext.setName(dataIdentifier + "_teamcenter_name");
        teamCenterReviewContext.setContextId(dataIdentifier + "_teamcenter_context_id");
        teamCenterReviewContext.setStatus(dataIdentifier + "_teamcenter_status");
        reviewContexts.add(teamCenterReviewContext);

        ReviewContext sapMdgReviewContext = new ReviewContext();
        sapMdgReviewContext.setType("MDG-CR");
        sapMdgReviewContext.setName(dataIdentifier + "_sapmdg_name");
        sapMdgReviewContext.setContextId(dataIdentifier + "_sapmdg_context_id");
        sapMdgReviewContext.setStatus(dataIdentifier + "_sapmdg_status");
        reviewContexts.add(sapMdgReviewContext);

        review.setContexts(reviewContexts);

        return review;
    }

    private static Review addReviewTitle(Review review, String dataIdentifier) {
        review.setTitle(dataIdentifier + "_title");
        return review;
    }

    private static Review addReviewExecutor(Review review, String dataIdentifier) {

        com.example.mirai.libraries.core.model.User executor = new
                com.example.mirai.libraries.core.model.User();
        executor.setAbbreviation(dataIdentifier + "_executor_abbreviation");
        executor.setDepartmentName(dataIdentifier + "_executor_department_name");
        executor.setEmail(dataIdentifier + "_executor_email");
        executor.setFullName(dataIdentifier + "_executor_full_name");
        executor.setUserId(dataIdentifier + "_executor_user_id");
        review.setExecutor(executor);

        return review;
    }

    private static Review addReviewCreatedOn(Review review, String dataIdentifier) {
        review.setCreatedOn(getDate(dataIdentifier));
        return review;
    }

    private static ReviewEntry addReviewEntryCreatedOn(ReviewEntry reviewEntry, String dataIdentifier) {
        reviewEntry.setCreatedOn(getDate(dataIdentifier));
        return reviewEntry;
    }


    private static Review addReviewCreator(Review review, String dataIdentifier) {
        User creator = new User();
        creator.setAbbreviation(dataIdentifier + "_review-creator_abbreviation");
        creator.setDepartmentName(dataIdentifier + "_review-creator_department_name");
        creator.setEmail(dataIdentifier + "_review-creator_email");
        creator.setFullName(dataIdentifier + "_review-creator_full_name");
        creator.setUserId(dataIdentifier + "_review-creator_user_id");
        review.setCreator(creator);

        return review;
    }

    private static ReviewEntry addReviewEntryCreator(ReviewEntry reviewEntry, String dataIdentifier) {
        User creator = new User();
        creator.setAbbreviation(dataIdentifier + "_review-entry-creator_abbreviation");
        creator.setDepartmentName(dataIdentifier + "_review-entry-creator_department_name");
        creator.setEmail(dataIdentifier + "_review-entry-creator_email");
        creator.setFullName(dataIdentifier + "_review-entry-creator_full_name");
        creator.setUserId(dataIdentifier + "_review-entry-creator_user_id");
        reviewEntry.setCreator(creator);

        return reviewEntry;
    }

    private static Review addReviewCompletionDate(Review review, String dataIdentifier) {
        review.setCompletionDate(getDate(dataIdentifier));
        return review;
    }

    private static Date getDate(String dataIdentifier) {
        try {
            Long timestamp = Long.parseLong(dataIdentifier);
            return new Date(timestamp);
        } catch (NumberFormatException nfe) {
            return new Date();
        }
    }

    public static ReviewTask createReviewTask(String dataIdentifier, String properties) {
        ReviewTask reviewTask = new ReviewTask();
        switch (properties) {
            case Constants.ALL_PROPERTIES_EXCEPT_AUDIT:
                addReviewTaskAssignee(reviewTask, dataIdentifier);
                addReviewTaskDueDate(reviewTask, dataIdentifier);
                return reviewTask;
            case "ALL_PROPERTIES_EXCEPT_ASSIGNEE":
                addReviewTaskDueDate(reviewTask, dataIdentifier);
                addReviewTaskCreator(reviewTask, dataIdentifier);
                addReviewTaskCreatedOn(reviewTask, dataIdentifier);
                return reviewTask;
            case "ALL_PROPERTIES_EXCEPT_DUEDATE":
                addReviewTaskAssignee(reviewTask, dataIdentifier);
                addReviewTaskCreator(reviewTask, dataIdentifier);
                addReviewTaskCreatedOn(reviewTask, dataIdentifier);
                return reviewTask;
            case "ALL_PROPERTIES_EXCEPT_AUDIT_AND_ASSIGNEE":
                addReviewTaskDueDate(reviewTask, dataIdentifier);
                return reviewTask;
            case "ALL_PROPERTIES_EXCEPT_AUDIT_AND_DUEDATE":
                addReviewTaskAssignee(reviewTask, dataIdentifier);
                return reviewTask;
            case Constants.ALL_PROPERTIES:
            default:
                addReviewTaskAssignee(reviewTask, dataIdentifier);
                addReviewTaskDueDate(reviewTask, dataIdentifier);
                addReviewTaskCreator(reviewTask, dataIdentifier);
                addReviewTaskCreatedOn(reviewTask, dataIdentifier);
                return reviewTask;
        }
    }

    public static ReviewEntry addReviewEntryClassification(ReviewEntry reviewEntry, String dataIdentifier) {
        reviewEntry.setClassification(dataIdentifier + "_classification");
        return reviewEntry;
    }

    public static ReviewEntry addReviewEntryDescription(ReviewEntry reviewEntry, String dataIdentifier) {
        reviewEntry.setDescription(dataIdentifier + "_description");
        return reviewEntry;
    }

    public static ReviewEntry addReviewEntryRemark(ReviewEntry reviewEntry, String dataIdentifier) {
        reviewEntry.setRemark(dataIdentifier + "_remark");
        return reviewEntry;
    }

    public static ReviewEntry createReviewEntry(String dataIdentifier, String properties) {
        ReviewTask reviewTask = createReviewTask(dataIdentifier, properties);
        ReviewEntry reviewEntry = new ReviewEntry();
        switch (properties) {
            case Constants.ALL_PROPERTIES:
                addReviewEntryContext(dataIdentifier, reviewEntry);
                addReviewEntryCreator(reviewEntry, dataIdentifier);
                reviewEntry.setReviewTask(reviewTask);
                addReviewEntryClassification(reviewEntry, dataIdentifier);
                addReviewEntryDescription(reviewEntry, dataIdentifier);
                //addReviewEntryRemark(reviewEntry, dataIdentifier);
                addReviewEntryCreatedOn(reviewEntry, dataIdentifier);
                addReviewEntryAssignee(reviewEntry, dataIdentifier);
                return reviewEntry;
            case Constants.ALL_PROPERTIES_EXCEPT_AUDIT:
                addReviewEntryContext(dataIdentifier, reviewEntry);
                addReviewEntryCreator(reviewEntry, dataIdentifier);
                reviewEntry.setReviewTask(reviewTask);
                addReviewEntryClassification(reviewEntry, dataIdentifier);
                addReviewEntryDescription(reviewEntry, dataIdentifier);
                addReviewEntryAssignee(reviewEntry, dataIdentifier);
                return reviewEntry;
            case "ALL_PROPERTIES_EXCEPT_CLASSIFICATION":
                addReviewEntryContext(dataIdentifier, reviewEntry);
                addReviewEntryCreator(reviewEntry, dataIdentifier);
                reviewEntry.setReviewTask(reviewTask);
                addReviewEntryDescription(reviewEntry, dataIdentifier);
                addReviewEntryAssignee(reviewEntry, dataIdentifier);
                return reviewEntry;
            case "ALL_PROPERTIES_EXCEPT_DESCRIPTION":
                addReviewEntryContext(dataIdentifier, reviewEntry);
                addReviewEntryCreator(reviewEntry, dataIdentifier);
                reviewEntry.setReviewTask(reviewTask);
                addReviewEntryClassification(reviewEntry, dataIdentifier);
                addReviewEntryAssignee(reviewEntry, dataIdentifier);
                return reviewEntry;
            case "ALL_PROPERTIES_EXCEPT_ASSIGNEE":
                addReviewEntryContext(dataIdentifier, reviewEntry);
                addReviewEntryCreator(reviewEntry, dataIdentifier);
                reviewEntry.setReviewTask(reviewTask);
                addReviewEntryClassification(reviewEntry, dataIdentifier);
                addReviewEntryDescription(reviewEntry, dataIdentifier);
                addReviewEntryCreatedOn(reviewEntry, dataIdentifier);
                return reviewEntry;
            case "ALL_PROPERTIES_EXCEPT_REVIEW_TASK":
                addReviewEntryContext(dataIdentifier, reviewEntry);
                addReviewEntryCreator(reviewEntry, dataIdentifier);
                addReviewEntryClassification(reviewEntry, dataIdentifier);
                addReviewEntryDescription(reviewEntry, dataIdentifier);
                addReviewEntryCreatedOn(reviewEntry, dataIdentifier);
                addReviewEntryAssignee(reviewEntry, dataIdentifier);
                return reviewEntry;
            default:
                return reviewEntry;
        }
    }

    @NotNull
    public static ReviewEntry addReviewEntryContext(String dataIdentifier, ReviewEntry reviewEntry) {
        List<ReviewEntryContext> contexts = new ArrayList<>();
        ReviewEntryContext solutionItemContext = new ReviewEntryContext();
        solutionItemContext.setContextId(dataIdentifier);
        solutionItemContext.setType("SOLUTIONITEM");
        solutionItemContext.setName(dataIdentifier + "_solution_item_name");
        contexts.add(solutionItemContext);
        reviewEntry.setContexts(contexts);
        return reviewEntry;
    }

    private static ReviewTask addReviewTaskAssignee(ReviewTask reviewTask, String dataIdentifier) {
        User assignee = new User();
        assignee.setAbbreviation(dataIdentifier + "_review-task-assignee_abbreviation");
        assignee.setDepartmentName(dataIdentifier + "_review-task-assignee_department_name");
        assignee.setEmail(dataIdentifier + "_review-task-assignee_email");
        assignee.setFullName(dataIdentifier + "_review-task-assignee_full_name");
        assignee.setUserId(dataIdentifier + "_review-task-assignee_user_id");
        reviewTask.setAssignee(assignee);
        return reviewTask;
    }

    private static ReviewEntry addReviewEntryAssignee(ReviewEntry reviewEntry, String dataIdentifier) {
        User assignee = new User();
        assignee.setAbbreviation(dataIdentifier + "_review-entry-assignee_abbreviation");
        assignee.setDepartmentName(dataIdentifier + "_review-entry-assignee_department_name");
        assignee.setEmail(dataIdentifier + "_review-entry-assignee_email");
        assignee.setFullName(dataIdentifier + "_review-entry-assignee_full_name");
        assignee.setUserId(dataIdentifier + "_review-entry-assignee_user_id");
        reviewEntry.setAssignee(assignee);
        return reviewEntry;
    }

    private static ReviewTask addReviewTaskCreator(ReviewTask reviewTask, String dataIdentifier) {
        User creator = new User();
        creator.setAbbreviation(dataIdentifier + "_review-task-creator_abbreviation");
        creator.setDepartmentName(dataIdentifier + "_review-task-creator_department_name");
        creator.setEmail(dataIdentifier + "_review-task-creator_email");
        creator.setFullName(dataIdentifier + "_review-task-creator_full_name");
        creator.setUserId(dataIdentifier + "_review-task-creator_user_id");
        reviewTask.setCreator(creator);
        return reviewTask;
    }

    private static ReviewTask addReviewTaskCreatedOn(ReviewTask reviewTask, String dataIdentifier) {
        reviewTask.setCreatedOn(new Date());
        return reviewTask;
    }

    private static ReviewTask addReviewTaskDueDate(ReviewTask reviewTask, String dataIdentifier) {
        reviewTask.setDueDate(new Timestamp(getDate(dataIdentifier).getTime()));
        return reviewTask;
    }

    public static List<SolutionItem> createSolutionItemList() {

        SolutionItem solutionItem1 = new SolutionItem();
        solutionItem1.setId("pdr9ggDTQS4FxA");
        solutionItem1.setSolutionItemId("4022.683.22941");
        solutionItem1.setName("SR BT M10 FLAT HRHD +7");
        solutionItem1.setDescription("SR BT M10 FLAT HRHD +7");
        solutionItem1.setChangeType(null);
        solutionItem1.setConfigRelevantIndicator(null);
        solutionItem1.setMaterialGroup(null);
        solutionItem1.setOwner(null);
        solutionItem1.setMaterialType(null);
        solutionItem1.setRevision(null);
        solutionItem1.setSapChangeControlled(null);
        solutionItem1.setSerializationIndicator(null);
        solutionItem1.setSapChangeControlled(null);
        solutionItem1.setSerialNumberProfile(null);
        solutionItem1.setServiceMaterialPartIndicator(null);
        solutionItem1.setSolutionItems(null);
        solutionItem1.setSourceMaterial(null);
        solutionItem1.setTcChangeControlled(null);
        solutionItem1.setCrossPlantStatus(null);
        solutionItem1.setTpds(null);

        SolutionItem solutionItem2 = new SolutionItem();
        solutionItem2.setId("4fo9ggDTQS4FxA");
        solutionItem2.setSolutionItemId("ANCI.683.22941");
        solutionItem2.setName("SR BT M10 FLAT HRHD +7 ANCI");
        solutionItem2.setDescription("SR BT M10 FLAT HRHD +7 ANCI");
        solutionItem2.setChangeType(null);
        solutionItem2.setConfigRelevantIndicator(null);
        solutionItem2.setMaterialGroup(null);
        solutionItem2.setOwner(null);
        solutionItem2.setMaterialType(null);
        solutionItem2.setRevision(null);
        solutionItem2.setSapChangeControlled(null);
        solutionItem2.setSerializationIndicator(null);
        solutionItem2.setSapChangeControlled(null);
        solutionItem2.setSerialNumberProfile(null);
        solutionItem2.setServiceMaterialPartIndicator(null);
        solutionItem2.setSolutionItems(null);
        solutionItem2.setSourceMaterial(null);
        solutionItem2.setTcChangeControlled(null);
        solutionItem2.setCrossPlantStatus(null);
        solutionItem2.setTpds(null);

        List<SolutionItem> solutionItemsList = new ArrayList<SolutionItem>();
        solutionItemsList.add(solutionItem1);
        solutionItemsList.add(solutionItem2);
        return solutionItemsList;
    }

    public static List<Tpd> createTpd() {

        Tpd tpd1 = new Tpd();
        tpd1.setId("199-001-01");
        tpd1.setName("4022.683.22941-199-001-01");
        tpd1.setDescription("Purchasing specification");
        tpd1.setRevision(null);
        tpd1.setDocumentLink("https://ics-host.example.com/awc/#/com.siemens.splm.clientfx.tcui.xrt.showObject?uid=wOr9RczVQS4FxA");
        tpd1.setDocumentPart("001");
        tpd1.setDocumentStatus(null);
        tpd1.setDocumentType("PDF");
        tpd1.setDocumentVersion("01");
        tpd1.setNewChangeNumber(null);

        Tpd tpd2 = new Tpd();
        tpd2.setId("195-001-01");
        tpd2.setName("4022.683.22941-195-001-01");
        tpd2.setDescription("Pack/lock/transport/storage specification");
        tpd2.setRevision(null);
        tpd2.setDocumentLink("https://ics-host.example.com/awc/#/com.siemens.splm.clientfx.tcui.xrt.showObject?uid=wih9RczVQS4FxA");
        tpd2.setDocumentPart("001");
        tpd2.setDocumentStatus(null);
        tpd2.setDocumentType("PDF");
        tpd2.setDocumentVersion("01");
        tpd2.setNewChangeNumber(null);
        List<Tpd> tpdList = new ArrayList<Tpd>();
        tpdList.add(tpd1);
        tpdList.add(tpd2);
        return tpdList;

    }

    public static List<ReviewMaterialDelta> createMaterialList() {
        ReviewMaterialDelta reviewMaterial = new ReviewMaterialDelta();

        reviewMaterial.setId("RPHV.202.22203");
        reviewMaterial.setMaterialType(null);
        reviewMaterial.setSourcingPlant(null);
        reviewMaterial.setToolsPackagingCategory(null);
        reviewMaterial.setToolsPackagingCategoryDescription(null);
        reviewMaterial.setFailureRate(null);
        reviewMaterial.setRegularPartDescription(null);
        reviewMaterial.setSerialNumberProfile(null);
        reviewMaterial.setDefectCount(0);
        PlantSpecificDelta plantSpecific = new PlantSpecificDelta();
        plantSpecific.setPlantName("US21");
        plantSpecific.setDiscoIndicator(null);
        plantSpecific.setDiscoDescription(null);
        plantSpecific.setInHousePdt(null);
        plantSpecific.setRefMaterial(null);
        plantSpecific.setObjectDependency(null);
        plantSpecific.setSpecProcurementType(null);
        plantSpecific.setSpecProcurementTypeDescription(null);
        plantSpecific.setPlantStatus(null);
        plantSpecific.setPdt(null);
        plantSpecific.setMrpProfile(null);
        plantSpecific.setMrpController(null);
        plantSpecific.setFollowupMaterial(null);

        List<PlantSpecificDelta> plantSpecificList = new ArrayList<>();
        plantSpecificList.add(plantSpecific);
        reviewMaterial.setPlantSpecific(plantSpecificList);

        List<ReviewMaterialDelta> materialList = new ArrayList<>();
        materialList.add(reviewMaterial);
        return materialList;
    }


    public static List<ReviewSolutionItemDelta> createSolutionItemDeltaList() {

        ReviewSolutionItemDelta solutionItemDelta = new ReviewSolutionItemDelta();
        solutionItemDelta.setId("4022.683.22941");
        solutionItemDelta.setSolutionItemId("4022.683.22941");
        solutionItemDelta.setName("SR BT M10 FLAT HRHD +7");
        SolutionItemDelta.Delta deltaRevision = new SolutionItemDelta.Delta(null, "AA");
        solutionItemDelta.setRevision(deltaRevision);
        SolutionItemDelta.Delta deltaDescription = new SolutionItemDelta.Delta(null, "SR BT M10 FLAT HRHD +7");
        solutionItemDelta.setDescription(deltaDescription);
        SolutionItemDelta.Delta deltaChangeType = new SolutionItemDelta.Delta(null, "New Part");
        solutionItemDelta.setChangeType(deltaChangeType);
        SolutionItemDelta.Delta deltaCrossPlantStatus = new SolutionItemDelta.Delta(null, "R2");
        solutionItemDelta.setCrossPlantStatus(deltaCrossPlantStatus);
        SolutionItemDelta.Delta deltaConfigRelevantIndicator = new SolutionItemDelta.Delta(null, "False");
        solutionItemDelta.setConfigRelevantIndicator(deltaConfigRelevantIndicator);
        SolutionItemDelta.Delta deltaServiceMaterialPartIndicator = new SolutionItemDelta.Delta(null, "No");
        solutionItemDelta.setServiceMaterialPartIndicator(deltaServiceMaterialPartIndicator);
        SolutionItemDelta.Delta deltaSapChangeControlled = new SolutionItemDelta.Delta(null, "False");
        solutionItemDelta.setSapChangeControlled(deltaSapChangeControlled);
        SolutionItemDelta.Delta deltaTcChangeControlled = new SolutionItemDelta.Delta(null, "True");
        solutionItemDelta.setTcChangeControlled(deltaTcChangeControlled);
        SolutionItemDelta.Delta deltaOwner = new SolutionItemDelta.Delta(null, "cosmeets");
        solutionItemDelta.setOwner(deltaOwner);
        SolutionItemDelta.Delta deltaMaterialType = new SolutionItemDelta.Delta(null, "ASSY");
        solutionItemDelta.setMaterialType(deltaMaterialType);
        SolutionItemDelta.Delta deltaSerializationIndicator = new SolutionItemDelta.Delta(null, "S");
        solutionItemDelta.setSerializationIndicator(deltaSerializationIndicator);
        SolutionItemDelta.Delta deltaMaterialGroup = new SolutionItemDelta.Delta(null, "100");
        solutionItemDelta.setMaterialGroup(deltaMaterialGroup);
        SolutionItemDelta.Delta deltaSerialNumberProfile = new SolutionItemDelta.Delta(null, "ZB01");
        solutionItemDelta.setSerialNumberProfile(deltaSerialNumberProfile);
        SolutionItemDelta.Delta deltaSourceMaterial = new SolutionItemDelta.Delta(null, "REF-ABST-SL");
        solutionItemDelta.setSourceMaterial(deltaSourceMaterial);
        solutionItemDelta.setUnitOfMeasure(null);
        solutionItemDelta.setProjectCode(null);
        solutionItemDelta.setProcurementType(null);
        solutionItemDelta.setStepperModel(null);

        List<ReviewSolutionItemDelta> solutionItemDeltaList = new ArrayList<>();
        solutionItemDeltaList.add(solutionItemDelta);
        return solutionItemDeltaList;
    }

    public static List<MaterialDelta> createMaterialDeltaList(List<ReviewSolutionItemDelta> mockSolutionItemDeltaList, List<ReviewMaterialDelta> mockMaterialList) {

        MaterialDelta materialDelta = new MaterialDelta();
        materialDelta.setId(mockSolutionItemDeltaList.get(0).getSolutionItemId());
        materialDelta.setTeamcenterSolutionItem(mockSolutionItemDeltaList.get(0));
        materialDelta.setSapMdgSolutionItem(mockMaterialList.get(0));
        MaterialDelta.WarningMessage warningMessage = new MaterialDelta.WarningMessage();
        warningMessage.setTitle("4 Empty Values");
        List<String> warningMessageList = new ArrayList<>();
        warningMessageList.add("unit_of_material\nproject_code\nstepper_model\nprocurement_type");
        warningMessage.setMessage(warningMessageList);
        List<MaterialDelta.WarningMessage> warningMessagesList = new ArrayList<>();
        warningMessagesList.add(warningMessage);
        materialDelta.setWarningMessages(warningMessagesList);
        materialDelta.setDefectCount(0);
        List<MaterialDelta> materialDeltaList = new ArrayList<>();
        materialDeltaList.add(materialDelta);
        return materialDeltaList;
    }


    public static List<SolutionItemSummary> createSolutionItemSummaryList() {

        SolutionItemSummary solutionItemSummary1 = new SolutionItemSummary();

        solutionItemSummary1.setId("4022.683.22941");
        solutionItemSummary1.setTitle("SR BT M10 FLAT HRHD +7");
        List<String> typeList1 = new ArrayList<>();
        typeList1.add("Teamcenter");
        typeList1.add("SAP MDG");
        solutionItemSummary1.setSourceSystem(typeList1);

        SolutionItemSummary solutionItemSummary2 = new SolutionItemSummary();
        solutionItemSummary2.setId("ANCI.683.22941");
        solutionItemSummary2.setTitle("SR BT M10 FLAT HRHD +7 ANCI");
        List<String> typeList2 = new ArrayList<>();
        typeList2.add("Teamcenter");
        typeList2.add("SAP MDG");
        solutionItemSummary2.setSourceSystem(typeList2);

        SolutionItemSummary solutionItemSummary3 = new SolutionItemSummary();
        solutionItemSummary3.setId("RPHV.202.22203");
        solutionItemSummary3.setTitle("RPHV.202.22203");
        List<String> typeList3 = new ArrayList<>();
        typeList3.add("SAP MDG");
        solutionItemSummary3.setSourceSystem(typeList3);

        SolutionItemSummary solutionItemSummary4 = new SolutionItemSummary();
        solutionItemSummary4.setId("ANCI.683.22801");
        solutionItemSummary4.setTitle("SR BT M10 FLAT HRHD -7 ANCI");
        List<String> typeList4 = new ArrayList<>();
        typeList4.add("Teamcenter");
        solutionItemSummary4.setSourceSystem(typeList4);

        List<SolutionItemSummary> solutionItemSummaryList = new ArrayList<>();
        solutionItemSummaryList.add(solutionItemSummary1);
        solutionItemSummaryList.add(solutionItemSummary2);
        solutionItemSummaryList.add(solutionItemSummary3);
        solutionItemSummaryList.add(solutionItemSummary4);
        return solutionItemSummaryList;
    }

}
