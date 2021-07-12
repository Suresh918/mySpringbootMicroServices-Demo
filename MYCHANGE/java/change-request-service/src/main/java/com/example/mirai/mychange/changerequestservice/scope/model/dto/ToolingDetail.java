package com.example.mirai.projectname.changerequestservice.scope.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class ToolingDetail implements Serializable {
    private String supplierTooling;
    private String manufacturingDeTooling;
    private String serviceTooling;

    public ToolingDetail(ToolingDetail toolingDetail) {
        this.supplierTooling = toolingDetail.getSupplierTooling();
        this.manufacturingDeTooling = toolingDetail.getManufacturingDeTooling();
        this.serviceTooling = toolingDetail.getServiceTooling();
    }
}
