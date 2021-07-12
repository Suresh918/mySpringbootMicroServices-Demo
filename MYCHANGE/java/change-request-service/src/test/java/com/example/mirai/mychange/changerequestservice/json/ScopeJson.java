package com.example.mirai.projectname.changerequestservice.json;

import com.example.mirai.projectname.changerequestservice.scope.model.dto.PackagingDetail;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.PartDetail;
import com.example.mirai.projectname.changerequestservice.scope.model.dto.ToolingDetail;

public class ScopeJson extends Content {
    public ScopeJson(String content) {
        super(content);
    }

    public Long getId() {
        return Long.valueOf("" + documentContext.read("id"));
    }

    public Boolean getScopeDetails() {
        return documentContext.read("scopeDetails");
    }

    public Boolean getParts() {
        return documentContext.read("parts");
    }

    public Boolean getIsSecure() {
        return documentContext.read("isSecure");
    }

    public PartDetail getPartDetail() {
        PartDetail partDetail = new PartDetail();
        partDetail.setDevBagPart(getDevBagPart());
        partDetail.setFcoUpgradeOptionCsr(getFcoUpgradeOptionCsr());
        partDetail.setMachineBomPart(getMachineBomPart());
        partDetail.setPreinstallPart(getPreinstallPart());
        partDetail.setServicePart(getServicePart());
        partDetail.setTestRigPart(getTestRigPart());
        return partDetail;
    }

    private String getTestRigPart() {
        return documentContext.read("$.part_detail.test_rig_part");
    }

    private String getServicePart() {
        return documentContext.read("$.part_detail.service_part");
    }

    private String getPreinstallPart() {
        return documentContext.read("$.part_detail.preinstall_part");
    }

    private String getMachineBomPart() {
        return documentContext.read("$.part_detail.machine_bom_part");
    }

    private String getFcoUpgradeOptionCsr() {
        return documentContext.read("$.part_detail.fco_upgrade_option_csr");
    }

    private String getDevBagPart() {
        return documentContext.read("$.part_detail.dev_bag_part");
    }

    public ToolingDetail getToolingDetail() {
        ToolingDetail toolingDetail = new ToolingDetail();
        toolingDetail.setManufacturingDeTooling(getManufacturingDeTooling());
        toolingDetail.setServiceTooling(getServiceTooling());
        toolingDetail.setSupplierTooling(getSupplierTooling());
        return toolingDetail;
    }

    private String getSupplierTooling() {
        return documentContext.read("$.tooling_detail.supplier_tooling");
    }

    private String getServiceTooling() {
        return documentContext.read("$.tooling_detail.service_tooling");
    }

    private String getManufacturingDeTooling() {
        return documentContext.read("$.tooling_detail.manufacturing_de_tooling");
    }

    public PackagingDetail getPackagingDetail() {
        PackagingDetail packagingDetail = new PackagingDetail();
        packagingDetail.setReusablePackaging(getReusablePackaging());
        packagingDetail.setShippingPackaging(getShippingPackaging());
        packagingDetail.setStoragePackaging(getStoragePackaging());
        packagingDetail.setSupplierPackaging(getSupplierPackaging());
        return packagingDetail;
    }

    private String getSupplierPackaging() {
        return documentContext.read("$.packaging_detail.reusable_packaging");
    }

    private String getStoragePackaging() {
        return documentContext.read("$.packaging_detail.shipping_packaging");
    }

    private String getShippingPackaging() {
        return documentContext.read("$.packaging_detail.storage_packaging");
    }

    private String getReusablePackaging() {
        return documentContext.read("$.packaging_detail.supplier_packaging");
    }
}
