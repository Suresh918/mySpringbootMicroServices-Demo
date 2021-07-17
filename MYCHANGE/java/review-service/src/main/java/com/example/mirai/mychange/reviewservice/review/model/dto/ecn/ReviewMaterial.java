package com.example.mirai.projectname.reviewservice.review.model.dto.ecn;

import com.example.mirai.libraries.sapmdg.material.model.Material;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewMaterial extends Material {
    Integer defectCount;

    public static ReviewMaterial copy(Material sourceMaterial, ReviewMaterial targetMaterial) {
        targetMaterial.setId(sourceMaterial.getId());
        targetMaterial.setMaterialType(sourceMaterial.getMaterialType());
        targetMaterial.setPlantSpecific(sourceMaterial.getPlantSpecific());
        targetMaterial.setSerialNumberProfile(sourceMaterial.getSerialNumberProfile());
        targetMaterial.setSourcingPlant(sourceMaterial.getSourcingPlant());
        targetMaterial.setToolsPackagingCategory(sourceMaterial.getToolsPackagingCategory());
        targetMaterial.setToolsPackagingCategoryDescription(sourceMaterial.getToolsPackagingCategoryDescription());
        targetMaterial.setFailureRate(sourceMaterial.getFailureRate());
        return targetMaterial;
    }
}
