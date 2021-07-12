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
public class PartDetail implements Serializable {
    private String machineBomPart;
    private String servicePart;
    private String preinstallPart;
    private String testRigPart;
    private String devBagPart;
    private String fcoUpgradeOptionCsr;

    public PartDetail(PartDetail partDetail) {
        this.machineBomPart = partDetail.getMachineBomPart();
        this.servicePart = partDetail.getServicePart();
        this.preinstallPart = partDetail.getPreinstallPart();
        this.testRigPart = partDetail.getTestRigPart();
        this.devBagPart = partDetail.getDevBagPart();
        this.fcoUpgradeOptionCsr = partDetail.getFcoUpgradeOptionCsr();
    }
}
