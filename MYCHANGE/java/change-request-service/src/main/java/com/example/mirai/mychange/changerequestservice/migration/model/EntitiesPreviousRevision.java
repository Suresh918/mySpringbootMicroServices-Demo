package com.example.mirai.projectname.changerequestservice.migration.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EntitiesPreviousRevision {
    Integer changeRequestRevision;
    Integer scopeRevision;
    Integer impactAnalysisRevision;
    Integer solutionDefinitionRevision;
    Integer customerImpactRevision;
    Integer preinstallImpactRevision;
    Integer completeBusinessCaseRevision;
}
