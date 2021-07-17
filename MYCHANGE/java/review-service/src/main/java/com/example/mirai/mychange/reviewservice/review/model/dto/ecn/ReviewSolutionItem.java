package com.example.mirai.projectname.reviewservice.review.model.dto.ecn;

//import com.example.mirai.libraries.teamcenter.ecn.model.SolutionItem;
import com.example.mirai.libraries.deltareport.model.dto.SolutionItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSolutionItem extends SolutionItem {
    Integer defectCount;
    String changeTypeLabel;
    List<ReviewSolutionItem> reviewSolutionItems;

    public static ReviewSolutionItem copy(SolutionItem sourceSolutionItem, ReviewSolutionItem targetSourceSolutionItem) {
        targetSourceSolutionItem.setId(sourceSolutionItem.getId());
        targetSourceSolutionItem.setSolutionItemId(sourceSolutionItem.getSolutionItemId());
        targetSourceSolutionItem.setName(sourceSolutionItem.getName());
        targetSourceSolutionItem.setTpds(sourceSolutionItem.getTpds());
        targetSourceSolutionItem.setRevision(sourceSolutionItem.getRevision());
        targetSourceSolutionItem.setDescription(sourceSolutionItem.getDescription());
        targetSourceSolutionItem.setChangeType(sourceSolutionItem.getChangeType());
        targetSourceSolutionItem.setChangeTypeLabel(SolutionItemChangeType.getLabelByCode(sourceSolutionItem.getChangeType()));
        targetSourceSolutionItem.setCrossPlantStatus(sourceSolutionItem.getCrossPlantStatus());
        targetSourceSolutionItem.setConfigRelevantIndicator(sourceSolutionItem.getConfigRelevantIndicator());
        targetSourceSolutionItem.setServiceMaterialPartIndicator(sourceSolutionItem.getServiceMaterialPartIndicator());
        targetSourceSolutionItem.setSapChangeControlled(sourceSolutionItem.getSapChangeControlled());
        targetSourceSolutionItem.setTcChangeControlled(sourceSolutionItem.getTcChangeControlled());
        targetSourceSolutionItem.setOwner(sourceSolutionItem.getOwner());
        targetSourceSolutionItem.setMaterialType(sourceSolutionItem.getMaterialType());
        targetSourceSolutionItem.setSerializationIndicator(sourceSolutionItem.getSerializationIndicator());
        targetSourceSolutionItem.setMaterialGroup(sourceSolutionItem.getMaterialGroup());
        targetSourceSolutionItem.setSerialNumberProfile(sourceSolutionItem.getSerialNumberProfile());
        targetSourceSolutionItem.setSourceMaterial(sourceSolutionItem.getSourceMaterial());
        targetSourceSolutionItem.setUnitOfMeasure(sourceSolutionItem.getUnitOfMeasure());
        targetSourceSolutionItem.setProjectCode(sourceSolutionItem.getProjectCode());
        targetSourceSolutionItem.setStepperModel(sourceSolutionItem.getStepperModel());
        targetSourceSolutionItem.setProcurementType(sourceSolutionItem.getProcurementType());
        targetSourceSolutionItem.setOccurrenceOrderNumber(sourceSolutionItem.getOccurrenceOrderNumber());
        targetSourceSolutionItem.setQuantity(sourceSolutionItem.getQuantity());
        targetSourceSolutionItem.setSolutionItems(sourceSolutionItem.getSolutionItems());

        return targetSourceSolutionItem;
    }

    public void addReviewSolutionItem(ReviewSolutionItem reviewSolutionItem) {
        if (reviewSolutionItems == null)
            reviewSolutionItems = new ArrayList<ReviewSolutionItem>();
        reviewSolutionItems.add(reviewSolutionItem);
    }
}
