package com.example.mirai.projectname.changerequestservice.json;

public class SolutionDefinitionJson extends Content {
    public SolutionDefinitionJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }

    private Integer getFunctionalSoftwareDependencies() {
        return documentContext.read("implementation_priority");
    }

    private Integer getFunctionalSoftwareDependenciesDetails() {
        return documentContext.read("implementation_priority");
    }

    private Integer getFunctionalHardwareDependencies() {
        return documentContext.read("implementation_priority");
    }

    private Integer getFunctionalHardwareDependenciesDetails() {
        return documentContext.read("implementation_priority");
    }

    private Integer getHardwareSoftwareDependenciesAligned() {
        return documentContext.read("implementation_priority");
    }

    private Integer getHardwareSoftwareDependenciesAlignedDetails() {
        return documentContext.read("implementation_priority");
    }

    private Integer getTestAndReleaseStrategy() {
        return documentContext.read("implementation_priority");
    }

    private Integer getTestAndReleaseStrategyDetails() {
        return documentContext.read("implementation_priority");
    }

    private Integer getProductsAffected() {
        return documentContext.read("implementation_priority");
    }

    private Integer getProductsModuleAffected() {
        return documentContext.read("implementation_priority");
    }

    private Integer getTechnicalRecommendation() {
        return documentContext.read("implementation_priority");
    }


}
