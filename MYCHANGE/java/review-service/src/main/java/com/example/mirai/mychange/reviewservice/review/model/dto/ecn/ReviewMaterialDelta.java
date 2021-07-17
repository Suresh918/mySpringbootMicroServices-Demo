package com.example.mirai.projectname.reviewservice.review.model.dto.ecn;

import com.example.mirai.libraries.sapmdg.material.model.Material;
import com.example.mirai.libraries.sapmdg.material.model.MaterialDelta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewMaterialDelta extends MaterialDelta {
    Integer defectCount;

    public static ReviewMaterialDelta copy(MaterialDelta sourceReviewMaterialDelta, ReviewMaterialDelta targetReviewMaterialDelta) {
        targetReviewMaterialDelta.setId(sourceReviewMaterialDelta.getId());
        targetReviewMaterialDelta.setMaterialType(sourceReviewMaterialDelta.getMaterialType());
        targetReviewMaterialDelta.setRegularPartDescription(sourceReviewMaterialDelta.getRegularPartDescription());
        targetReviewMaterialDelta.setPlantSpecific(sourceReviewMaterialDelta.getPlantSpecific());
        targetReviewMaterialDelta.setSerialNumberProfile(sourceReviewMaterialDelta.getSerialNumberProfile());
        targetReviewMaterialDelta.setSourcingPlant(sourceReviewMaterialDelta.getSourcingPlant());
        targetReviewMaterialDelta.setToolsPackagingCategory(sourceReviewMaterialDelta.getToolsPackagingCategory());
        targetReviewMaterialDelta.setToolsPackagingCategoryDescription(sourceReviewMaterialDelta.getToolsPackagingCategoryDescription());
        targetReviewMaterialDelta.setFailureRate(sourceReviewMaterialDelta.getFailureRate());
        return targetReviewMaterialDelta;
    }
}
