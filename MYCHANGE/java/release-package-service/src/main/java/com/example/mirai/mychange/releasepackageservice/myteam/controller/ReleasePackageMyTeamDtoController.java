package com.example.mirai.projectname.releasepackageservice.myteam.controller;

import java.util.List;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.model.dto.TeamDetails;
import com.example.mirai.projectname.releasepackageservice.core.component.EntityResolver;
import com.example.mirai.projectname.releasepackageservice.myteam.model.aggregate.ReleasePackageMyTeamAggregate;
import com.example.mirai.projectname.releasepackageservice.myteam.service.ReleasePackageMyTeamService;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;
import lombok.Data;
import lombok.SneakyThrows;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("{parentType:release-packages}")
@Data
public class ReleasePackageMyTeamDtoController {

    private final ReleasePackageMyTeamService releasePackageMyTeamService;
    private final ReleasePackageService releasePackageService;
    private final EntityResolver entityResolver;

    @SneakyThrows
    @GetMapping(value = "/{releasePackageId:[0-9]+}/{entityType:my-team}", params = "view=detail")
    @ResponseStatus(HttpStatus.OK)
    public TeamDetails getTeamDetail(@PathVariable Long releasePackageId) {
        return releasePackageMyTeamService.getTeamDetails(releasePackageId);
    }


    @SneakyThrows
    @GetMapping(value = "/{ecn:^ECN-[0-9]+}/{entityType:my-team}", params = "view=detail")
    @ResponseStatus(HttpStatus.OK)
    public TeamDetails getTeamDetailByEcn(@PathVariable String ecn) {
        Long releasePackageId = releasePackageService.getReleasePackageIdByContext(ecn, "ECN");
        return releasePackageMyTeamService.getTeamDetails(releasePackageId);
    }

    @SneakyThrows
    @GetMapping(value = "/{releasePackageNumber:[0-9]+-[0-9]+}/{entityType:my-team}", params = "view=detail")
    @ResponseStatus(HttpStatus.OK)
    public TeamDetails getTeamDetailByReleasePackageNumber(@PathVariable String releasePackageNumber) {
        Long releasePackageId = releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber);
        return releasePackageMyTeamService.getTeamDetails(releasePackageId);
    }

    @GetMapping(value = "/{releasePackageNumber:[0-9]+-[0-9]+}/{entityType:my-team}/my-team-members")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<MyTeamMember> getReleasePackageMyTeamMembersByNumber(@PathVariable String releasePackageNumber) {
        Long releasePackageId = releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber);
        return releasePackageMyTeamService.getAllMembersOfMyTeam(releasePackageId);
    }

    @GetMapping(value = "/{ecn:^ECN-[0-9]+}/{entityType:my-team}/my-team-members")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public List<MyTeamMember> getReleasePackageMyTeamMembersByEcn(@PathVariable String ecn) {
        Long releasePackageId = releasePackageService.getReleasePackageIdByContext(ecn, "ECN");
        return releasePackageMyTeamService.getAllMembersOfMyTeam(releasePackageId);
    }

    @GetMapping(value="/{ecn:^ECN-[0-9]+}/{entityType:my-team}", params = {"view=aggregate"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public AggregateInterface getAggregateByEcn(@PathVariable String parentType, @PathVariable String entityType, @PathVariable String ecn) {
        Long releasePackageId = releasePackageService.getReleasePackageIdByContext(ecn, "ECN");
        return releasePackageMyTeamService.getAggregateByParent(releasePackageId, (Class) ReleasePackageMyTeamAggregate.class);
    }


    @GetMapping(value = "/{releasePackageNumber:[0-9]+-[0-9]+}/{entityType:my-team}", params = {"view=aggregate"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public AggregateInterface getAggregateByReleasePackageNumber(@PathVariable String parentType, @PathVariable String entityType, @PathVariable String releasePackageNumber) {
        Long releasePackageId = releasePackageService.getReleasePackageIdByReleasePackageNumber(releasePackageNumber);
        return releasePackageMyTeamService.getAggregateByParent(releasePackageId, (Class) ReleasePackageMyTeamAggregate.class);
    }
}
