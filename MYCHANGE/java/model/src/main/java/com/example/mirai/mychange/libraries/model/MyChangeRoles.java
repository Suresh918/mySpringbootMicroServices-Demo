package com.example.mirai.projectname.libraries.model;

import com.example.mirai.libraries.core.model.Roles;

import java.util.Arrays;

public enum MyChangeRoles implements Roles {
    businessController("businessController", "Business Controller"),
    changeSpecialist1("changeSpecialist1", "Change Specialist 1"),
    changeSpecialist2("changeSpecialist2", "Change Specialist 2"),
    changeSpecialist3("changeSpecialist3", "Change Specialist 3"),
    cmFieldRepresentative("cmFieldRepresentative", "CM Field representative"),
    cmLeadEngineerRepresentative("cmLeadEngineerRepresentative", "CM Lead Engineer representative"),
    cmManufacturingRepresentative("cmManufacturingRepresentative", "CM Manufacturing representative"),
    coordinatorSCMPLM("coordinatorSCMPLM", "Coordinator SCM PLM"),
    developmentAndEngineeringArchitect("development&EngineeringArchitect", "D&E Architect"),
    developmentAndEngineeringGroupLead("development&EngineeringGroupLead", "Development & Engineering Group Lead"),
    developmentAndEngineeringProjectLead("development&EngineeringProjectLead", "Development & Engineering Project Lead"),
    ecnExecutor("ecnExecutor", "ECN executor"),
    headOfCSGroup("headOfCSGroup", "Head of CS Group"),
    headOfDETeam("headOfDETeam", "Head of DE Team"),
    headOfProductionOperation("headOfProductionOperation", "Head of Production Operation"),
    headOfSystemIntegrationGroup("headOfSystemIntegrationGroup", "Head of System Integration Group"),
    other("other", "Other"),
    productDevelopmentManager("productDevelopmentManager", "Product Development Manager"),
    productManager("productManager", "Product Manager"),
    productSystemEngineer("productSystemEngineer", "Product System Engineer"),
    scmPLMProjectManager("scmPLMProjectManager", "SCM PLM Project Manager"),
    sourcingLead("sourcingLead", "Sourcing Lead"),
    sourcingPL("sourcingPL", "Sourcing PL"),
    submitterRequestor("submitterRequestor", "Submitter/Requestor"),
    supplyChainEngineer("supplyChainEngineer", "Supply Chain Engineer"),
    creator("creator", "Creator"),
    user("user", "User"),
    changeOwner("changeOwner", "Change Owner");

    private final String role;
    private final String label;

    MyChangeRoles(final String role, final String label) {
        this.role = role;
        this.label = label;
    }


    @Override
    public String getRole() {
        return role;
    }

    public String getLabel() {
        return label;
    }

    public static String getLabel(String role) {
        return Arrays.stream(MyChangeRoles.values()).filter(myChangeRole -> myChangeRole.getRole().equals(role)).findFirst().get().getLabel();
    }
}
