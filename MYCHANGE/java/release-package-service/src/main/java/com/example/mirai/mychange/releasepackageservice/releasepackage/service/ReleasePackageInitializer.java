package com.example.mirai.projectname.releasepackageservice.releasepackage.service;


import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.example.mirai.libraries.entity.model.Id;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ChangeOwnerType;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageStatus;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageAggregate;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.aggregate.ReleasePackageMyTeamDetailsAggregate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ReleasePackageInitializer {
    @Autowired
    private ReleasePackageService releasePackageService;

    public void initializeReleasePackage(ReleasePackageAggregate aggregate) {
        initializeReleasePackage(aggregate.getReleasePackage());

        ReleasePackageMyTeamDetailsAggregate releasePackageMyTeamDetailsAggregate = new ReleasePackageMyTeamDetailsAggregate();
        releasePackageMyTeamDetailsAggregate.setMembers(new HashSet<>());
        releasePackageMyTeamDetailsAggregate.setMyTeam(new ReleasePackageMyTeam());
        aggregate.setMyTeamDetails(releasePackageMyTeamDetailsAggregate);
    }

    public void initializeReleasePackage(ReleasePackage releasePackage) {
        if (Objects.isNull(releasePackage.getChangeOwnerType())) {
            releasePackage.setChangeOwnerType(ChangeOwnerType.PROJECT.name());
        }
        if(Objects.nonNull(releasePackage.getChangeOwnerType()) && releasePackage.getChangeOwnerType().equalsIgnoreCase("CREATOR")){
            releasePackage.setSapChangeControl(false);
        }
        releasePackage.setStatus(ReleasePackageStatus.DRAFTED.getStatusCode());
        List<ReleasePackageContext> releasePackageContexts = releasePackage.getContexts();

        if (releasePackageContexts == null || releasePackageContexts.stream().filter(context -> context.getType().equals("CHANGENOTICE")).count() != 1) {
            throw new RuntimeException("Exactly One Change Notice Needed In Context");  //CreateNewException
        }

        Optional<ReleasePackageContext> releasePackageChangeNoticeContext = releasePackageContexts.stream().filter(context -> context.getType().equals("CHANGENOTICE")).findFirst();
        synchronized (this) {
            setReleasePackageSequenceNumber(releasePackage, releasePackageChangeNoticeContext.get().getContextId());
            addEcnToContexts(releasePackage);
        }

    }

    private void setReleasePackageSequenceNumber(ReleasePackage releasePackage, String changeNoticeId) {
        String sequenceNumber;
        Slice<Id> idSlice = releasePackageService.filterIds("contexts.contextId:" + changeNoticeId, PageRequest.of(0, 1000, Sort.by("id").descending()));
        if (Objects.isNull(idSlice) || idSlice.getNumberOfElements() == 0) {
            sequenceNumber = "01";
        } else {
			long id = idSlice.getContent().stream().count() + 1;
            sequenceNumber = (id < 10) ? String.format("%02d", id) : Long.toString(id);
        }
        releasePackage.setReleasePackageNumber(changeNoticeId + "-" + sequenceNumber);
    }

    private void addEcnToContexts(ReleasePackage releasePackage) {
        ReleasePackageContext context = new ReleasePackageContext();
        String ecnNumber = "ECN-" + releasePackageService.getSequenceNumberForEcn();
        context.setType("ECN");
        context.setName(ecnNumber);
        context.setContextId(ecnNumber);
        releasePackage.getContexts().add(context);
    }

}
