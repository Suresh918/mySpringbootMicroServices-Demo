package com.example.mirai.projectname.releasepackageservice.releasepackage.service.helper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;

public class ReleasePackageUtil {

    public static String getReleasePackageContextId(final ReleasePackage releasePackage, String contextType) {
        List<ReleasePackageContext> releasePackageContextList = releasePackage.getContexts();
        if (Objects.nonNull(releasePackageContextList)) {
            Optional<ReleasePackageContext> releasePackageContext = releasePackageContextList.stream().filter(context -> context.getType().toUpperCase().equals(contextType)).findFirst();
            if (releasePackageContext.isPresent()) {
                return releasePackageContext.get().getContextId();
            }
        }
        return null;
    }

    public static String getReleasePackageContextStatus(final ReleasePackage releasePackage, String contextType) {
        List<ReleasePackageContext> releasePackageContextList = releasePackage.getContexts();
        if (Objects.nonNull(releasePackageContextList)) {
            Optional<ReleasePackageContext> releasePackageContext = releasePackageContextList.stream().filter(context -> context.getType().toUpperCase().equals(contextType)).findFirst();
            if (releasePackageContext.isPresent()) {
                return releasePackageContext.get().getStatus();
            }
        }
        return null;
    }

    public static ReleasePackageContext getReleasePackageContext(final ReleasePackage releasePackage, String contextType) {
        List<ReleasePackageContext> releasePackageContextList = releasePackage.getContexts();
        if (Objects.nonNull(releasePackageContextList)) {
            Optional<ReleasePackageContext> releasePackageContext = releasePackageContextList.stream().filter(context -> context.getType().toUpperCase().equals(contextType)).findFirst();
            if (releasePackageContext.isPresent()) {
                return releasePackageContext.get();
            }
        }
        return null;
    }

}
