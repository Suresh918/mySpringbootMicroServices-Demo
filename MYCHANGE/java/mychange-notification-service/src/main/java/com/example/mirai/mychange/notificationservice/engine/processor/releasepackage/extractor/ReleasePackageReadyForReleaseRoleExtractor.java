package com.example.mirai.projectname.notificationservice.engine.processor.releasepackage.extractor;


import com.example.mirai.libraries.core.model.Event;
import com.example.mirai.libraries.notification.engine.processor.BaseRole;
import com.example.mirai.libraries.notification.engine.processor.RoleExtractorInterface;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;

@Component("ReleasePackageReadyForRelease")
public class ReleasePackageReadyForReleaseRoleExtractor extends ReleasePackageRoleExtractor implements RoleExtractorInterface {
    @Override
    public Set<BaseRole> getProcessors(Event event) throws IOException {
        this.category = "ReleasePackageReadyForRelease";
        return super.getProcessors(event);

    }
}
