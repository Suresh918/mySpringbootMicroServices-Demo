package com.example.mirai.projectname.releasepackageservice.shared;

import java.util.HashMap;
import java.util.Map;

import com.example.mirai.libraries.core.component.ApplicationContextHolder;
import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.model.dto.MyTeamPublishData;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.AutomaticClosureAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;

public class AggregateEventBuilder extends com.example.mirai.libraries.event.AggregateEventBuilder {
    @Override
    public Object translateResponse(Object obj) {
        if (obj instanceof MyTeamMember) {
            ReleasePackageMyTeam myTeam = (ReleasePackageMyTeam) ((MyTeamMember) obj).getMyteam();
            ReleasePackageService releasePackageService = (ReleasePackageService) ApplicationContextHolder.getService(ReleasePackageService.class);
            ReleasePackageAggregate releasePackageAggregate = releasePackageService.getAggregate(myTeam.getReleasePackage().getId());
            Map aggregateData = new HashMap<String, AggregateInterface>();
            aggregateData.put("release_package", releasePackageAggregate);
            return new MyTeamPublishData(aggregateData, (MyTeamMember) obj);
        } else if (obj instanceof ReleasePackageAggregate || obj instanceof AutomaticClosureAggregate) {
            return obj;
        }
        return super.translateResponse(obj);
    }
}
