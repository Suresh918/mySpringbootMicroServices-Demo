package com.example.mirai.projectname.releasepackageservice.document.service;

import com.example.mirai.libraries.core.model.BaseEvaluationContext;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageDocument;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReleasePackageDocumentEvaluationContext extends BaseEvaluationContext<ReleasePackageDocument> {

    public ReleasePackage getReleasePackage() {
        return context.getReleasePackage();
    }

    public Boolean isReleasePackageSecure() {
        ReleasePackage releasePackage = getReleasePackage();
        return releasePackage.getIsSecure() == true;
    }
}
