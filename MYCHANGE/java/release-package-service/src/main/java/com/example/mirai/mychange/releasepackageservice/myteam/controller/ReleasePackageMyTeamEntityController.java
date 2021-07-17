package com.example.mirai.projectname.releasepackageservice.myteam.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.example.mirai.libraries.myteam.controller.MyTeamEntityController;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.service.MyTeamService;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate.ReleasePackageMyTeamMemberAggregate;
import com.example.mirai.projectname.releasepackageservice.myteam.service.ReleasePackageMyTeamService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageDetail;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"{parentType:release-packages}/{entityType:my-team}"})
public class ReleasePackageMyTeamEntityController extends MyTeamEntityController {

    private final ReleasePackageMyTeamService releasePackageMyTeamService;

    public ReleasePackageMyTeamEntityController(ObjectMapper objectMapper, ReleasePackageMyTeamService releasePackageMyTeamService,MyTeamService myTeamService, EntityResolverDefaultInterface entityResolver) {
        super(objectMapper, myTeamService, entityResolver);
        this.releasePackageMyTeamService = releasePackageMyTeamService;
    }

    @Override
    public Class<? extends BaseEntityInterface> getEntityClass() {
        return ReleasePackageMyTeam.class;
    }

    @PatchMapping(value = "/my-team-members", params = "release-package-number")
    public ReleasePackageDetail.MyTeamDetail addImpactedItemMyTeamMember(@RequestParam(name="release-package-number") String releasePackageNumber, @RequestBody JsonNode jsonNode) {
        ReleasePackageMyTeamMemberAggregate[] changeObjectMyTeamMembers = objectMapper.convertValue(jsonNode, ReleasePackageMyTeamMemberAggregate[].class);
        List<MyTeamMember> myTeamMembers = Arrays.asList(changeObjectMyTeamMembers).stream().map(memberAggregate -> memberAggregate.getMember()).collect(Collectors.toList());
        return releasePackageMyTeamService.updateMyTeamForCreatorsAndUsers(releasePackageNumber, myTeamMembers);
    }

    @PatchMapping(value = "/my-team-members", params = {"release-package-numbers", "role=changeOwner"})
    public void updateImpactedItemMyTeamMemberWithRole(@RequestParam(name="release-package-numbers") String[] releasePackageNumbers,
                                                                                    @RequestBody JsonNode jsonNode) {
        User myTeamMemberUser = objectMapper.convertValue(jsonNode, User.class);
        releasePackageMyTeamService.updateRoleForMembersOfReleasePackages(releasePackageNumbers, myTeamMemberUser);
    }

}
