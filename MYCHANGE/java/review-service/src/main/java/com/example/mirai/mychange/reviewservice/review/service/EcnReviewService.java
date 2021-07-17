package com.example.mirai.projectname.reviewservice.review.service;

import com.example.mirai.libraries.core.exception.InternalAssertionException;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.deltareport.model.dto.SolutionItem;
import com.example.mirai.libraries.deltareport.model.dto.SolutionItemDelta;
import com.example.mirai.libraries.deltareport.model.dto.Tpd;
import com.example.mirai.libraries.deltareport.service.DeltaReportService;
import com.example.mirai.libraries.sapmdg.changerequest.model.DeleteMaterialResponse;
import com.example.mirai.libraries.sapmdg.changerequest.service.SapMdgChangeRequestService;
import com.example.mirai.libraries.sapmdg.material.model.Material;
import com.example.mirai.libraries.sapmdg.material.service.SapMdgMaterialService;
import com.example.mirai.libraries.teamcenter.ecn.service.EcnService;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewContext;
import com.example.mirai.projectname.reviewservice.review.model.dto.ReviewEntryContextCount;
import com.example.mirai.projectname.reviewservice.review.model.dto.ecn.*;
import com.example.mirai.projectname.reviewservice.review.repository.ReviewRepository;
import com.example.mirai.projectname.reviewservice.shared.exception.MdgCrContextNotExistException;
import com.example.mirai.projectname.reviewservice.shared.exception.ZecnReviewException;
import com.example.mirai.projectname.reviewservice.shared.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
public class EcnReviewService {
    private ReviewRepository reviewRepository;
    private DeltaReportService deltaReportService;
    private EcnService ecnService;
    private SapMdgMaterialService sapMdgMaterialService;
    private SapMdgChangeRequestService sapMdgChangeRequestService;
    @Autowired
    private ReviewService reviewService;

    public EcnReviewService (EcnService ecnService,DeltaReportService deltaReportService,SapMdgMaterialService sapMdgMaterialService,SapMdgChangeRequestService sapMdgChangeRequestService,ReviewRepository reviewRepository){
        this.ecnService = ecnService;
        this.deltaReportService = deltaReportService;
        this.sapMdgChangeRequestService = sapMdgChangeRequestService;
        this.reviewRepository = reviewRepository;
        this.sapMdgMaterialService =  sapMdgMaterialService;
    }

    public static List<String> checkNullValueFields(ReviewSolutionItemDelta reviewSolutionItemDelta,ReviewMaterialDelta reviewMaterial) {
        List<String> fieldNames = new ArrayList<>();
        if(Objects.nonNull(reviewSolutionItemDelta)) {
            if (Objects.isNull(reviewSolutionItemDelta.getId()) || reviewSolutionItemDelta.getId().equals(""))
                fieldNames.add("id");
            if (Objects.isNull(reviewSolutionItemDelta.getSolutionItemId()) || reviewSolutionItemDelta.getSolutionItemId().equals(""))
                fieldNames.add("solution_item_id");
            if (Objects.isNull(reviewSolutionItemDelta.getName()) || reviewSolutionItemDelta.getName().equals(""))
                fieldNames.add("name");
            if (Objects.isNull(reviewSolutionItemDelta.getRevision()) || Objects.isNull(reviewSolutionItemDelta.getRevision().getNewValue()) || reviewSolutionItemDelta.getRevision().getNewValue().equals(""))
                fieldNames.add("revision");
            if (Objects.isNull(reviewSolutionItemDelta.getDescription()) || Objects.isNull(reviewSolutionItemDelta.getDescription().getNewValue()) || reviewSolutionItemDelta.getDescription().getNewValue().equals(""))
                fieldNames.add("description");
            if (Objects.isNull(reviewSolutionItemDelta.getChangeType()) || Objects.isNull(reviewSolutionItemDelta.getChangeType().getNewValue()) || reviewSolutionItemDelta.getChangeType().getNewValue().equals(""))
                fieldNames.add("change_type");
            if (Objects.isNull(reviewSolutionItemDelta.getCrossPlantStatus()) || Objects.isNull(reviewSolutionItemDelta.getCrossPlantStatus().getNewValue()) || reviewSolutionItemDelta.getCrossPlantStatus().getNewValue().equals(""))
                fieldNames.add("cross_plant_status");
            if (Objects.isNull(reviewSolutionItemDelta.getConfigRelevantIndicator()) || Objects.isNull(reviewSolutionItemDelta.getConfigRelevantIndicator().getNewValue()) || reviewSolutionItemDelta.getConfigRelevantIndicator().getNewValue().equals(""))
                fieldNames.add("config_relevant_indicator");
            if (Objects.isNull(reviewSolutionItemDelta.getServiceMaterialPartIndicator()) || Objects.isNull(reviewSolutionItemDelta.getServiceMaterialPartIndicator().getNewValue()) || reviewSolutionItemDelta.getServiceMaterialPartIndicator().getNewValue().equals(""))
                fieldNames.add("service_material_part_indicator");
            if (Objects.isNull(reviewSolutionItemDelta.getSapChangeControlled()) || Objects.isNull(reviewSolutionItemDelta.getSapChangeControlled().getNewValue()) || reviewSolutionItemDelta.getSapChangeControlled().getNewValue().equals(""))
                fieldNames.add("sap_change_controlled");
            if (Objects.isNull(reviewSolutionItemDelta.getTcChangeControlled()) || Objects.isNull(reviewSolutionItemDelta.getTcChangeControlled().getNewValue()) || reviewSolutionItemDelta.getTcChangeControlled().getNewValue().equals(""))
                fieldNames.add("tc_change_controlled");
            /*if (Objects.isNull(reviewSolutionItemDelta.getOwner()) || Objects.isNull(reviewSolutionItemDelta.getOwner().getNewValue()) || reviewSolutionItemDelta.getOwner().getNewValue().equals(""))
                fieldNames.add("owner");*/
            if (Objects.isNull(reviewSolutionItemDelta.getMaterialType()) || Objects.isNull(reviewSolutionItemDelta.getMaterialType().getNewValue()) || reviewSolutionItemDelta.getMaterialType().getNewValue().equals(""))
                fieldNames.add("material_type");
            if (Objects.isNull(reviewSolutionItemDelta.getSerializationIndicator()) || Objects.isNull(reviewSolutionItemDelta.getSerializationIndicator().getNewValue()) || reviewSolutionItemDelta.getSerializationIndicator().getNewValue().equals(""))
                fieldNames.add("serialization_indicator");
            if (Objects.isNull(reviewSolutionItemDelta.getMaterialGroup()) || Objects.isNull(reviewSolutionItemDelta.getMaterialGroup().getNewValue()) || reviewSolutionItemDelta.getMaterialGroup().getNewValue().equals(""))
                fieldNames.add("material_group");
            if (Objects.isNull(reviewSolutionItemDelta.getSerialNumberProfile()) || Objects.isNull(reviewSolutionItemDelta.getSerialNumberProfile().getNewValue()) || reviewSolutionItemDelta.getSerialNumberProfile().getNewValue().equals(""))
                fieldNames.add("serial_number_profile_tc");
            if (Objects.isNull(reviewSolutionItemDelta.getSourceMaterial()) || Objects.isNull(reviewSolutionItemDelta.getSourceMaterial().getNewValue()) || reviewSolutionItemDelta.getSourceMaterial().getNewValue().equals(""))
                fieldNames.add("source_material");
            if (Objects.isNull(reviewSolutionItemDelta.getUnitOfMeasure()) || Objects.isNull(reviewSolutionItemDelta.getUnitOfMeasure().getNewValue()) || reviewSolutionItemDelta.getUnitOfMeasure().getNewValue().equals(""))
                fieldNames.add("unit_of_measure");
            if (Objects.isNull(reviewSolutionItemDelta.getProjectCode()) || Objects.isNull(reviewSolutionItemDelta.getProjectCode().getNewValue()) || reviewSolutionItemDelta.getProjectCode().getNewValue().equals(""))
                fieldNames.add("project_code");
            if (Objects.isNull(reviewSolutionItemDelta.getStepperModel()) || Objects.isNull(reviewSolutionItemDelta.getStepperModel().getNewValue()) || reviewSolutionItemDelta.getStepperModel().getNewValue().equals(""))
                fieldNames.add("stepper_model");
            if (Objects.isNull(reviewSolutionItemDelta.getProcurementType()) || Objects.isNull(reviewSolutionItemDelta.getProcurementType().getNewValue()) || reviewSolutionItemDelta.getProcurementType().getNewValue().equals(""))
                fieldNames.add("procurement_type");
        }

        if(Objects.nonNull(reviewMaterial)){
            if (Objects.isNull(reviewMaterial.getMaterialType()) || Objects.isNull(reviewMaterial.getMaterialType().getNewValue()) || reviewMaterial.getMaterialType().getNewValue().equals("") )
                fieldNames.add("material_type");
            if (Objects.isNull(reviewMaterial.getRegularPartDescription()) || Objects.isNull(reviewMaterial.getRegularPartDescription().getNewValue()) || reviewMaterial.getRegularPartDescription().getNewValue().equals("") )
                fieldNames.add("regular_part_description");
            if (Objects.isNull(reviewMaterial.getSerialNumberProfile()) || Objects.isNull(reviewMaterial.getSerialNumberProfile().getNewValue()) || reviewMaterial.getSerialNumberProfile().getNewValue().equals("") )
                fieldNames.add("serial_number_profile");
            if (Objects.isNull(reviewMaterial.getSourcingPlant()) || Objects.isNull(reviewMaterial.getSourcingPlant().getNewValue()) || reviewMaterial.getSourcingPlant().getNewValue().equals("") )
                fieldNames.add("sourcing_plant");
            if (Objects.isNull(reviewMaterial.getToolsPackagingCategory()) || Objects.isNull(reviewMaterial.getToolsPackagingCategory().getNewValue()) || reviewMaterial.getToolsPackagingCategory().getNewValue().equals("") )
                fieldNames.add("tools_packaging_category");
            if (Objects.isNull(reviewMaterial.getFailureRate()) || Objects.isNull(reviewMaterial.getFailureRate().getNewValue()) || reviewMaterial.getFailureRate().getNewValue().equals("") )
                fieldNames.add("failure_rate");
            if (Objects.isNull(reviewMaterial.getPlantSpecific()))
                fieldNames.add("plant_specific");

        }

        return fieldNames;
    }

    private void checkForZecnReview(Long id) {

        Review currentReview = (Review) reviewService.getEntityById(id);
        Optional<ReviewContext> releasePackageContext = currentReview.getContexts().stream().filter(context -> context.getType().equals("RELEASEPACKAGE")).findFirst();
        String releasePackageId = null;
        if (releasePackageContext.isPresent())
            releasePackageId = releasePackageContext.get().getContextId();
        else
            throw new InternalAssertionException("Release Package context not found");

        BaseEntityList baseEntityList = reviewService.filter("contexts.contextId:" + releasePackageId + " and contexts.type:RELEASEPACKAGE", PageRequest.of(0, Integer.MAX_VALUE-1));
        List<Review> previousReviews = (List<Review>) baseEntityList.getResults().stream().filter(review -> (((Review)review).getCreatedOn().before(currentReview.getCreatedOn()) && ((Review) review).getId() <currentReview.getId())).collect(Collectors.toList());
        if (!previousReviews.isEmpty())
            throw new ZecnReviewException();
    }

    public List<ReviewSolutionItem> getSolutionItemList(Long id) {
        List<ReviewSolutionItem> reviewSolutionItemList = new ArrayList<>();
        String teamcenterId = getTeamcenterId(id);
        if (Objects.isNull(teamcenterId))
            return null;
        com.example.mirai.libraries.deltareport.model.SolutionItem solutionItem = deltaReportService.getSolutionItemByTeamCenterId(teamcenterId);
        if (Objects.isNull(solutionItem))
            return new ArrayList<>();
        List<com.example.mirai.libraries.deltareport.model.dto.SolutionItem> solutionItems = solutionItem.getSolutionItemList();
        String context = "12NC";
        List<ReviewEntryContextCount> reviewEntryContextCountList = reviewRepository.findReviewEntryContextCountByReviewId(id,context);
        Iterator<SolutionItem> iterator = solutionItems.iterator();
        iterator.forEachRemaining(itersolutionItem -> {
            ReviewSolutionItem reviewSolutionItem = new ReviewSolutionItem();
            buildReviewSolutionItems(itersolutionItem, reviewSolutionItem, reviewEntryContextCountList, false);
            reviewSolutionItemList.add(reviewSolutionItem);
        });
        return reviewSolutionItemList;
    }

    public List<ReviewSolutionItem> getReviewBomStructure(Long id) {
        checkForZecnReview(id);
        List<ReviewSolutionItem> reviewSolutionItemList = new ArrayList<>();
        String teamcenterId = getTeamcenterId(id);
        if (Objects.isNull(teamcenterId))
            return null;
        com.example.mirai.libraries.deltareport.model.SolutionItem solutionItem = deltaReportService.getSolutionItemByTeamCenterId(teamcenterId);
        if (Objects.isNull(solutionItem))
            return new ArrayList<>();
        List<SolutionItem> solutionItems = solutionItem.getSolutionItemList();
        String context = "12NC";
        List<ReviewEntryContextCount> reviewEntryContextCountList = reviewRepository.findReviewEntryContextCountByReviewId(id,context);
        Iterator<SolutionItem> iterator = solutionItems.iterator();
        iterator.forEachRemaining(iterSolutionItem -> {
            ReviewSolutionItem reviewSolutionItem = new ReviewSolutionItem();
            buildReviewSolutionItems(iterSolutionItem, reviewSolutionItem, reviewEntryContextCountList, true);
            reviewSolutionItemList.add(reviewSolutionItem);
        });
        return reviewSolutionItemList;
    }

    private ReviewSolutionItem buildReviewSolutionItems(SolutionItem solutionItem, final ReviewSolutionItem reviewSolutionItem, final List<ReviewEntryContextCount> reviewEntryContextCountList, boolean buildHierarchy) {
        ReviewSolutionItem.copy(solutionItem, reviewSolutionItem);
        Optional<ReviewEntryContextCount> optionalReviewEntryContextCount = reviewEntryContextCountList.stream().filter(reviewEntryContextCount -> reviewEntryContextCount.getId().equals(solutionItem.getSolutionItemId())).findFirst();
        if (optionalReviewEntryContextCount.isPresent())
            reviewSolutionItem.setDefectCount(optionalReviewEntryContextCount.get().getCount());
        else
            reviewSolutionItem.setDefectCount(0);
        if (buildHierarchy && solutionItem.getSolutionItems() != null && solutionItem.getSolutionItems().size() > 0) {
            Iterator<SolutionItem> iterator = solutionItem.getSolutionItems().iterator();
            iterator.forEachRemaining(iterSolutionItem -> {
                ReviewSolutionItem childReviewSolutionItem = new ReviewSolutionItem();
                buildReviewSolutionItems(iterSolutionItem, childReviewSolutionItem, reviewEntryContextCountList, true);
                reviewSolutionItem.addReviewSolutionItem(childReviewSolutionItem);
            });
        }
        return reviewSolutionItem;
    }

    public List<ReviewTpd> getReviewTpds(Long id) {
        checkForZecnReview(id);
        List<ReviewTpd> reviewTpdList = new ArrayList<>();
        String teamcenterId = getTeamcenterId(id);
        if (Objects.isNull(teamcenterId))
            return null;
        com.example.mirai.libraries.deltareport.model.Tpd tpd = deltaReportService.getTpdByTeamCenterId(teamcenterId);
        if (Objects.isNull(tpd)) {
            return new ArrayList<>();
        }
        List<com.example.mirai.libraries.deltareport.model.dto.Tpd> tpdList = tpd.getTpdList();
        String context = "12NC";
        List<ReviewEntryContextCount> reviewEntryContextCountList = reviewRepository.findReviewEntryContextCountByReviewId(id,context);
        Iterator<Tpd> iterator = tpdList.iterator();
        iterator.forEachRemaining(iterTpd -> {
            ReviewTpd reviewTpd = new ReviewTpd();
            buildTpd(iterTpd, reviewTpd, reviewEntryContextCountList);
            reviewTpdList.add(reviewTpd);
        });
        return reviewTpdList;
    }

    public List<ReviewMaterial> getReviewMaterials(Long id) {
        List<ReviewMaterial> reviewMaterialList = new ArrayList<>();
        String sapId = getSapId(id);
        if (Objects.isNull(sapId)) {
            return reviewMaterialList;
        }
        List<Material> materials = sapMdgMaterialService.getMaterialList(sapId);
        String context = "12NC";
        List<ReviewEntryContextCount> reviewEntryContextCountList = reviewRepository.findReviewEntryContextCountByReviewId(id,context);
        Iterator<Material> iterator = materials.iterator();
        iterator.forEachRemaining(iterMaterial -> {
            ReviewMaterial reviewMaterial = new ReviewMaterial();
            buildMaterial(iterMaterial, reviewMaterial, reviewEntryContextCountList);
            reviewMaterialList.add(reviewMaterial);
        });
        return reviewMaterialList;
    }

    public List<ReviewMaterialDelta> getReviewMaterialsDelta(Long id) {
        List<ReviewMaterialDelta> reviewMateriaDeltalList = new ArrayList<>();
        String sapId = getSapId(id);
        if (Objects.isNull(sapId)) {
            return reviewMateriaDeltalList;
        }
        List<com.example.mirai.libraries.sapmdg.material.model.MaterialDelta> materials = sapMdgMaterialService.getMaterialDeltaList(sapId);
        String context = "12NC";
        List<ReviewEntryContextCount> reviewEntryContextCountList = reviewRepository.findReviewEntryContextCountByReviewId(id,context);
        Iterator<com.example.mirai.libraries.sapmdg.material.model.MaterialDelta> iterator = materials.iterator();
        iterator.forEachRemaining(iterMaterialDelta -> {
            ReviewMaterialDelta  reviewMaterialDelta = new ReviewMaterialDelta();
            buildReviewMaterialDelta(iterMaterialDelta, reviewMaterialDelta, reviewEntryContextCountList);
            reviewMateriaDeltalList.add(reviewMaterialDelta);
        });
        return reviewMateriaDeltalList;
    }

    private ReviewMaterial buildMaterial(Material material, final ReviewMaterial reviewMaterial, final List<ReviewEntryContextCount> reviewEntryContextCountList) {
        ReviewMaterial.copy(material, reviewMaterial);
        Optional<ReviewEntryContextCount> optionalReviewEntryContextCount = reviewEntryContextCountList.stream().filter(reviewEntryContextCount -> reviewEntryContextCount.getId().startsWith(material.getId())).findFirst();
        if (optionalReviewEntryContextCount.isPresent())
            reviewMaterial.setDefectCount(optionalReviewEntryContextCount.get().getCount());
        else
            reviewMaterial.setDefectCount(0);
        return reviewMaterial;
    }

    private ReviewMaterialDelta buildReviewMaterialDelta(com.example.mirai.libraries.sapmdg.material.model.MaterialDelta materialDelta, final ReviewMaterialDelta reviewMaterialDelta, final List<ReviewEntryContextCount> reviewEntryContextCountList) {
        ReviewMaterialDelta.copy(materialDelta, reviewMaterialDelta);
        Optional<ReviewEntryContextCount> optionalReviewEntryContextCount = reviewEntryContextCountList.stream().filter(reviewEntryContextCount -> reviewEntryContextCount.getId().startsWith(materialDelta.getId())).findFirst();
        if (optionalReviewEntryContextCount.isPresent())
            reviewMaterialDelta.setDefectCount(optionalReviewEntryContextCount.get().getCount());
        else
            reviewMaterialDelta.setDefectCount(0);
        return reviewMaterialDelta;
    }

    public List<MaterialDelta> getMaterialDeltaList(Long id) {
        String teamcenterId = getTeamcenterId(id);
        if (!Objects.isNull(teamcenterId)){
            checkForZecnReview(id);
        }
        List<MaterialDelta> materialList = new ArrayList<>();
        //get solutionitems from teamcenter
        List<ReviewSolutionItemDelta> reviewSolutionItemDeltaList = getReviewSolutionItemDelta(id);
        String sapMdgContextId = getSapId(id);
        if (Objects.isNull(sapMdgContextId)) {
            reviewSolutionItemDeltaList.stream().forEach(reviewSolutionItemDelta -> {

                MaterialDelta materialDelta = new MaterialDelta();
                materialDelta.setId(reviewSolutionItemDelta.getSolutionItemId());
                materialDelta.setTeamcenterSolutionItem(reviewSolutionItemDelta);
                materialDelta.setSapMdgSolutionItem(null);
                materialDelta.setDefectCount(reviewSolutionItemDelta.getDefectCount());
                List<String> fieldNames = getFieldsWithNullValue(reviewSolutionItemDelta,null);
                checkTeamcenterGroupWarning(fieldNames,materialDelta);
                materialDelta.setShowSapBaseGroupWarning(false);
                materialDelta.setShowSapEnrichmentGroupWarning(false);
                List<MaterialDelta.WarningMessage> warningMessages = new ArrayList<>();
                if (fieldNames.size() != 0) {
                    warningMessages.add(new MaterialDelta.WarningMessage(fieldNames.size() + " Empty Values",
                            fieldNames));
                }
                materialList.add(materialDelta);
            });
            return materialList;
        }
        //get materials from sap mdg
        List<ReviewMaterialDelta> reviewMaterialList = getReviewMaterialsDelta(id);

        if (Objects.isNull(reviewMaterialList)) {
            log.info("sap mdg material list not valid");
            reviewMaterialList = new ArrayList<>();
        }
        if (Objects.isNull(reviewSolutionItemDeltaList)) {
            log.info("Teamcenter solution list not valid");
            reviewSolutionItemDeltaList = new ArrayList<>();
        }
        List<ReviewSolutionItemDelta> finalReviewSolutionItemDeltaList = reviewSolutionItemDeltaList;
        reviewMaterialList.stream().forEach(reviewMaterial -> {
            Optional<ReviewSolutionItemDelta> optionalReviewSolutionItemDelta = finalReviewSolutionItemDeltaList.stream()
                    .filter(reviewSolutionItemDelta -> Objects.equals(reviewSolutionItemDelta.getSolutionItemId(), reviewMaterial.getId())).findFirst();

            MaterialDelta materialDelta = new MaterialDelta();
            materialDelta.setId(reviewMaterial.getId());
            materialDelta.setSapMdgSolutionItem(reviewMaterial);
            materialDelta.setDefectCount(reviewMaterial.getDefectCount());
            List<MaterialDelta.WarningMessage> warningMessages = new ArrayList<>();

            if (optionalReviewSolutionItemDelta.isPresent()) {
                ReviewSolutionItemDelta reviewSolutionItemDelta = optionalReviewSolutionItemDelta.get();
                List<String> fieldNames = getFieldsWithNullValue(reviewSolutionItemDelta,reviewMaterial);
                materialDelta.setTeamcenterSolutionItem(optionalReviewSolutionItemDelta.get());
                checkTeamcenterGroupWarning(fieldNames,materialDelta);
                checkSapBaseGroupWarning(reviewMaterial,materialDelta);
                checkSapEnrichmentGroupWarning(reviewMaterial,materialDelta);
                if (fieldNames.size() != 0) {
                    String warningTitle = fieldNames.size() + " Empty Values";
                    warningMessages.add(new MaterialDelta.WarningMessage(warningTitle, fieldNames));
                }
            } else {
                List<String> warningMessageList = new ArrayList<>();
                List<String> fieldNames = getFieldsWithNullValue(null,reviewMaterial);
                materialDelta.setShowTeamcenterGroupWarning(false);
                checkSapBaseGroupWarning(reviewMaterial,materialDelta);
                checkSapEnrichmentGroupWarning(reviewMaterial,materialDelta);
                warningMessageList.add(Constants.MATERIAL_MISSING_IN_TEAMCENTER_MESSAGE);
                warningMessages.add(new MaterialDelta.WarningMessage(Constants.MATERIAL_MISSING_IN_TEAMCENTER_TITLE,
                        warningMessageList));
            }
            materialDelta.setWarningMessages(warningMessages);
            materialList.add(materialDelta);
        });

        List<ReviewMaterialDelta> finalReviewMaterialList = reviewMaterialList;
        reviewSolutionItemDeltaList.stream().forEach(reviewSolutionItemDelta -> {
            Optional<ReviewMaterialDelta> optionalReviewMaterial = finalReviewMaterialList.stream()
                    .filter(reviewMaterial -> Objects.equals(reviewMaterial.getId(), reviewSolutionItemDelta.getSolutionItemId())).findFirst();
            if (optionalReviewMaterial.isEmpty()) {
                MaterialDelta materialDelta = new MaterialDelta();
                materialDelta.setId(reviewSolutionItemDelta.getSolutionItemId());
                materialDelta.setTeamcenterSolutionItem(reviewSolutionItemDelta);
                materialDelta.setSapMdgSolutionItem(null);
                materialDelta.setDefectCount(reviewSolutionItemDelta.getDefectCount());
                List<String> fieldNames = getFieldsWithNullValue(reviewSolutionItemDelta,null);
                checkTeamcenterGroupWarning(fieldNames,materialDelta);
                materialDelta.setShowSapBaseGroupWarning(false);
                materialDelta.setShowSapEnrichmentGroupWarning(false);
                List<MaterialDelta.WarningMessage> warningMessages = new ArrayList<>();
                if (fieldNames.size() != 0) {
                    warningMessages.add(new MaterialDelta.WarningMessage(fieldNames.size() + " Empty Values",
                            fieldNames));
                }
                List<String> warningMessageList = new ArrayList<>();
                warningMessageList.add(Constants.MATERIAL_MISSING_IN_SAPMDG_MESSAGE);
                warningMessages.add(new MaterialDelta.WarningMessage(Constants.MATERIAL_MISSING_IN_SAPMDG_TITLE,
                        warningMessageList));
                materialDelta.setWarningMessages(warningMessages);
                materialList.add(materialDelta);
            }
        });

        return materialList;
    }

    public List<String> getFieldsWithNullValue(ReviewSolutionItemDelta reviewSolutionItemDelta, ReviewMaterialDelta reviewMaterial) {
        List<String> fieldNames = new ArrayList<>();
        fieldNames = checkNullValueFields(reviewSolutionItemDelta,reviewMaterial);
        return fieldNames;
    }

    public List<ReviewSolutionItemDelta> getReviewSolutionItemDelta(Long id) {
        List<ReviewSolutionItemDelta> reviewSolutionItemDeltaList = new ArrayList<>();
        String teamcenterId = getTeamcenterId(id);
        com.example.mirai.libraries.deltareport.model.SolutionItemDelta  solutionItemDelta = deltaReportService.getSolutionItemDeltaByTeamCenterId(teamcenterId);
        if (Objects.isNull(solutionItemDelta))
            return new ArrayList<>();
        List<SolutionItemDelta> solutionItemDeltaList = solutionItemDelta.getItemDeltaList();
        String context = "12NC";
        List<ReviewEntryContextCount> reviewEntryContextCountList = reviewRepository.findReviewEntryContextCountByReviewId(id,context);
        Iterator<SolutionItemDelta> iterator = solutionItemDeltaList.iterator();
        iterator.forEachRemaining(iterSolutionItemDelta -> {
            ReviewSolutionItemDelta reviewSolutionItemDelta = new ReviewSolutionItemDelta();
            buildSolutionItemDelta(iterSolutionItemDelta, reviewSolutionItemDelta, reviewEntryContextCountList);
            reviewSolutionItemDeltaList.add(reviewSolutionItemDelta);
        });
        return reviewSolutionItemDeltaList;
    }

    private void checkTeamcenterGroupWarning(List<String> fieldNames , MaterialDelta materialDelta ){
        if(fieldNames.contains("sourceMaterial") || fieldNames.contains("serviceMaterialPartIndicator")|| fieldNames.contains("materialType")||
                fieldNames.contains("procurementType")|| fieldNames.contains("materialGroup")|| fieldNames.contains("projectCode")|| fieldNames.contains("stepperModel")|| fieldNames.contains("unitOfMeasure")|| fieldNames.contains("serialNumberProfile")){
            materialDelta.setShowTeamcenterGroupWarning(true);
        }
    }

    private void checkSapBaseGroupWarning(ReviewMaterialDelta reviewMaterial, MaterialDelta materialDelta){
        if(Objects.nonNull(reviewMaterial.getMaterialType()) && Objects.isNull(reviewMaterial.getMaterialType().getNewValue()) ||
                Objects.nonNull(reviewMaterial.getSourcingPlant()) && Objects.isNull(reviewMaterial.getSourcingPlant().getNewValue()) ||
                Objects.nonNull(reviewMaterial.getFailureRate()) && Objects.isNull(reviewMaterial.getFailureRate().getNewValue()) ||
                Objects.nonNull(reviewMaterial.getSerialNumberProfile()) && Objects.isNull(reviewMaterial.getSerialNumberProfile().getNewValue()) ){
            materialDelta.setShowSapBaseGroupWarning(true);
        }
    }

    private void checkSapEnrichmentGroupWarning(ReviewMaterialDelta reviewMaterial, MaterialDelta materialDelta){
        if(Objects.nonNull(reviewMaterial.getPlantSpecific()) && (reviewMaterial.getPlantSpecific().size()!=0) ){
            reviewMaterial.getPlantSpecific().stream().forEach(plantSpecific->{
                if (    Objects.isNull(plantSpecific.getPlantName())  ||
                        Objects.nonNull(plantSpecific.getDiscoIndicator()) && Objects.isNull(plantSpecific.getDiscoIndicator().getNewValue()) ||
                        Objects.nonNull(plantSpecific.getInHousePdt()) && Objects.isNull(plantSpecific.getInHousePdt().getNewValue()) ||
                        Objects.nonNull(plantSpecific.getRefMaterial()) && Objects.isNull(plantSpecific.getRefMaterial().getNewValue())||
                        Objects.nonNull(plantSpecific.getSpecProcurementType()) && Objects.isNull(plantSpecific.getSpecProcurementType().getNewValue())||
                        Objects.nonNull(plantSpecific.getPlantStatus()) && Objects.isNull(plantSpecific.getPlantStatus().getNewValue())||
                        Objects.nonNull(plantSpecific.getPdt()) && Objects.isNull(plantSpecific.getPdt().getNewValue())||
                        Objects.nonNull(plantSpecific.getMrpProfile()) && Objects.isNull(plantSpecific.getMrpProfile().getNewValue())||
                        Objects.nonNull(plantSpecific.getMrpController()) && Objects.isNull(plantSpecific.getMrpController().getNewValue())||
                        Objects.nonNull(plantSpecific.getFollowupMaterial()) && Objects.isNull(plantSpecific.getFollowupMaterial().getNewValue())){
                    materialDelta.setShowSapEnrichmentGroupWarning(true);
                }
            });

        }

    }

    private ReviewSolutionItemDelta buildSolutionItemDelta(SolutionItemDelta solutionItemDelta, final ReviewSolutionItemDelta reviewSolutionItemDelta, final List<ReviewEntryContextCount> reviewEntryContextCountList) {
        ReviewSolutionItemDelta.copy(solutionItemDelta, reviewSolutionItemDelta);
        Optional<ReviewEntryContextCount> optionalReviewEntryContextCount = reviewEntryContextCountList.stream().filter(reviewEntryContextCount -> reviewEntryContextCount.getId().equals(solutionItemDelta.getSolutionItemId())).findFirst();
        if (optionalReviewEntryContextCount.isPresent())
            reviewSolutionItemDelta.setDefectCount(optionalReviewEntryContextCount.get().getCount());
        else
            reviewSolutionItemDelta.setDefectCount(0);
        return reviewSolutionItemDelta;
    }

    private ReviewTpd buildTpd(Tpd tpd, final ReviewTpd reviewTpd, final List<ReviewEntryContextCount> reviewEntryContextCountList) {
        ReviewTpd.copy(tpd, reviewTpd);
        Optional<ReviewEntryContextCount> optionalReviewEntryContextCount = reviewEntryContextCountList.stream().filter(reviewEntryContextCount -> reviewEntryContextCount.getId().equals(tpd.getCompleteTpdId())).findFirst();
        if (optionalReviewEntryContextCount.isPresent())
            reviewTpd.setDefectCount(optionalReviewEntryContextCount.get().getCount());
        else
            reviewTpd.setDefectCount(0);
        return reviewTpd;
    }

    private String getTeamcenterId(Long reviewId) {
        Review review = (Review) reviewService.get(reviewId);
        Optional<ReviewContext> optionalReviewContext = review.getContexts().stream().filter(context -> context.getType().equals("TEAMCENTER")).findFirst();
        if (optionalReviewContext.isPresent()) {
            ReviewContext reviewContext = optionalReviewContext.get();
            return reviewContext.getContextId();
        }
        return null;
    }

    private String getSapId(Long reviewId) {
        Review review = (Review) reviewService.get(reviewId);
        Optional<ReviewContext> optionalReviewContext = review.getContexts().stream().filter(context -> context.getType().equals("MDG-CR")).findFirst();
        if (optionalReviewContext.isPresent()) {
            ReviewContext reviewContext = optionalReviewContext.get();
            return reviewContext.getContextId();
        }
        //"216810", //"000000217321"//"000000217568"; //"000000217722"
        return null;
    }

    public List<SolutionItemSummary> getSolutionItemSummary(Long id) {
        List<SolutionItemSummary> solutionItemSummaries = new ArrayList<>();
        List<ReviewSolutionItem> reviewSolutionItemList = getSolutionItemList(id);
        //get materials from sap mdg
        List<ReviewMaterialDelta> reviewMaterialList = getReviewMaterialsDelta(id);
        reviewSolutionItemList.stream().forEach(reviewSolutionItem -> {
            SolutionItemSummary solutionItemSummary = new SolutionItemSummary(reviewSolutionItem);
            setSolutionItemSourceSystem(solutionItemSummary, reviewMaterialList);
            populateReviewSolutionItemChildren(solutionItemSummary, reviewSolutionItem.getSolutionItems(), reviewSolutionItem.getTpds(), reviewMaterialList);
            solutionItemSummaries.add(solutionItemSummary);
        });

        reviewMaterialList.stream().forEach(reviewMaterial -> {
            Optional<ReviewSolutionItem> optionalReviewSolutionItem = reviewSolutionItemList.stream()
                    .filter(reviewSolutionItem -> reviewSolutionItem.getSolutionItemId().equals(reviewMaterial.getId())).findFirst();
            SolutionItemSummary solutionItemSummary = new SolutionItemSummary();
            if (optionalReviewSolutionItem.isPresent()) {
                Optional<SolutionItemSummary> addedSolutionItemSummary = solutionItemSummaries.stream().filter(item -> item.getId().equals(optionalReviewSolutionItem.get().getSolutionItemId())).findFirst();
                if (addedSolutionItemSummary.isPresent()) {
                    List<String> type = new ArrayList<>(Arrays.asList("TEAMCENTER", "MATERIAL"));
                    addedSolutionItemSummary.get().setSourceSystem(type);
                }
            } else {
                solutionItemSummary.setId(reviewMaterial.getId());
                solutionItemSummary.setTitle(reviewMaterial.getId());
                List<String> type = new ArrayList<>(Arrays.asList("MATERIAL"));
                solutionItemSummary.setSourceSystem(type);
                solutionItemSummaries.add(solutionItemSummary);
            }
        });

        return solutionItemSummaries;
    }

    private void setSolutionItemSourceSystem(SolutionItemSummary solutionItemSummary, List<ReviewMaterialDelta> reviewMaterialList) {
        Optional<ReviewMaterialDelta> optionalReviewMaterial = reviewMaterialList.stream()
                .filter(reviewMaterial -> reviewMaterial.getId().equals(solutionItemSummary.getId())).findFirst();
        List<String> sourceSystem = new ArrayList<>(Arrays.asList("TEAMCENTER"));
        if (optionalReviewMaterial.isPresent()) {
            sourceSystem.add("MATERIAL");
        }
        solutionItemSummary.setSourceSystem(sourceSystem);
    }

    private void populateReviewSolutionItemChildren(SolutionItemSummary solutionItemSummary, List<SolutionItem> solutionItems, List<Tpd> tpds, List<ReviewMaterialDelta> reviewMaterialList) {
        List<SolutionItemSummary> solutionItemChildSummaries = new ArrayList<>();
        if (Objects.nonNull(solutionItems)) {
            solutionItems.stream().forEach(solutionItem -> {
                SolutionItemSummary solutionItemChildSummary = new SolutionItemSummary(solutionItem);//2
                setSolutionItemSourceSystem(solutionItemChildSummary, reviewMaterialList);
                solutionItemChildSummaries.add(solutionItemChildSummary);
                if (Objects.nonNull(solutionItem.getSolutionItems()) && !solutionItem.getSolutionItems().isEmpty())
                    populateReviewSolutionItemChildren(solutionItemChildSummary, solutionItem.getSolutionItems(), solutionItem.getTpds(), reviewMaterialList);
            });
        }
        if (Objects.nonNull(tpds))
            solutionItemChildSummaries.addAll(tpds.stream().map(tpd -> new SolutionItemSummary(tpd)).collect(Collectors.toList()));
        solutionItemSummary.setSolutionItems(solutionItemChildSummaries);
    }

    public DeleteMaterialResponse deleteMaterialByMaterialNumber(Long id, List<String> materialIds) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        String releasePackageContextId = "";
        if (optionalReview.isEmpty()) {
            return null;
        }
        releasePackageContextId = getContextIdByType(optionalReview.get(), "RELEASEPACKAGE");
        return sapMdgChangeRequestService.deleteMaterialByMaterialNumber(releasePackageContextId, materialIds);

    }

    private String getContextIdByType(Review review, String type) {
        Optional<ReviewContext> optionalReviewContext = review.getContexts().stream().filter(context -> context.getType().equals(type)).findFirst();
        if (optionalReviewContext.isEmpty()) {
            throw new MdgCrContextNotExistException();
        } else {
            return optionalReviewContext.get().getContextId();
        }
    }


}


