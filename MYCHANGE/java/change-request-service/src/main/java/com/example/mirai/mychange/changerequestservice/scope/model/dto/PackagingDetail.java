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
public class PackagingDetail implements Serializable {
    private String supplierPackaging;
    private String storagePackaging;
    private String shippingPackaging;
    private String reusablePackaging;

    public PackagingDetail(PackagingDetail packagingDetail) {
        this.supplierPackaging = packagingDetail.getSupplierPackaging();
        this.storagePackaging = packagingDetail.getStoragePackaging();
        this.shippingPackaging = packagingDetail.getShippingPackaging();
        this.reusablePackaging = packagingDetail.getReusablePackaging();
    }
}
