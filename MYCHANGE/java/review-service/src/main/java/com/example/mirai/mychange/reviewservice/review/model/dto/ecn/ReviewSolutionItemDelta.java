package com.example.mirai.projectname.reviewservice.review.model.dto.ecn;

//import com.example.mirai.libraries.teamcenter.ecn.model.SolutionItemDelta;
import com.example.mirai.libraries.deltareport.model.dto.SolutionItemDelta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewSolutionItemDelta extends SolutionItemDelta {

    Integer defectCount;
    String changeTypeLabel;

    public static ReviewSolutionItemDelta copy(SolutionItemDelta sourceSolutionItemDelta, ReviewSolutionItemDelta targetSolutionItemDelta) {

        targetSolutionItemDelta.setId(sourceSolutionItemDelta.getId());
        targetSolutionItemDelta.setSolutionItemId(sourceSolutionItemDelta.getSolutionItemId());
        targetSolutionItemDelta.setName(sourceSolutionItemDelta.getName());
        targetSolutionItemDelta.setRevision(sourceSolutionItemDelta.getRevision());
        targetSolutionItemDelta.setDescription(sourceSolutionItemDelta.getDescription());
        targetSolutionItemDelta.setChangeType(sourceSolutionItemDelta.getChangeType());
        if (Objects.nonNull(sourceSolutionItemDelta.getChangeType()))
            targetSolutionItemDelta.setChangeTypeLabel(SolutionItemChangeType.getLabelByCode(sourceSolutionItemDelta.getChangeType().getNewValue()));
        targetSolutionItemDelta.setCrossPlantStatus(sourceSolutionItemDelta.getCrossPlantStatus());
        targetSolutionItemDelta.setConfigRelevantIndicator(sourceSolutionItemDelta.getConfigRelevantIndicator());
        targetSolutionItemDelta.setServiceMaterialPartIndicator(sourceSolutionItemDelta.getServiceMaterialPartIndicator());
        targetSolutionItemDelta.setSapChangeControlled(sourceSolutionItemDelta.getSapChangeControlled());
        targetSolutionItemDelta.setTcChangeControlled(sourceSolutionItemDelta.getTcChangeControlled());
        //targetSolutionItemDelta.setOwner(sourceSolutionItemDelta.getOwner());
        targetSolutionItemDelta.setMaterialType(sourceSolutionItemDelta.getMaterialType());
        targetSolutionItemDelta.setSerializationIndicator(sourceSolutionItemDelta.getSerializationIndicator());
        targetSolutionItemDelta.setMaterialGroup(sourceSolutionItemDelta.getMaterialGroup());
        targetSolutionItemDelta.setSerialNumberProfile(sourceSolutionItemDelta.getSerialNumberProfile());
        targetSolutionItemDelta.setSourceMaterial(sourceSolutionItemDelta.getSourceMaterial());
        targetSolutionItemDelta.setUnitOfMeasure(sourceSolutionItemDelta.getUnitOfMeasure());
        targetSolutionItemDelta.setProjectCode(sourceSolutionItemDelta.getProjectCode());
        targetSolutionItemDelta.setStepperModel(sourceSolutionItemDelta.getStepperModel());
        targetSolutionItemDelta.setProcurementType(sourceSolutionItemDelta.getProcurementType());

        return targetSolutionItemDelta;
    }


}
