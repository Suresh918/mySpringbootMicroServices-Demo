package com.example.mirai.projectname.releasepackageservice.document.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Resource;

import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.document.model.dto.DocumentCategory;
import com.example.mirai.libraries.document.model.dto.DocumentOverview;
import com.example.mirai.libraries.document.repository.DocumentContentRepository;
import com.example.mirai.libraries.document.service.DocumentService;
import com.example.mirai.libraries.document.service.DocumentStateMachine;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.comment.service.ReleasePackageCommentService;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageDocument;
import com.example.mirai.projectname.releasepackageservice.document.repository.ReleasePackageDocumentRepository;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.service.ReleasePackageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Service;

@Service
@EntityClass(ReleasePackageDocument.class)
public class ReleasePackageDocumentService extends DocumentService {
    @Resource
    ReleasePackageDocumentService self;

    private ReleasePackageCommentDocumentService releasePackageCommentDocumentService;
    private final ReleasePackageCommentService releasePackageCommentService;
    private ReleasePackageDocumentRepository releasePackageDocumentRepository;
    private final DelegatingSecurityContextAsyncTaskExecutor executor;

    @Autowired
    private ReleasePackageService releasePackageService;
    public ReleasePackageDocumentService(DocumentStateMachine stateMachine, AbacProcessor abacProcessor,
                                         RbacProcessor rbacProcessor, EntityACL acl, PropertyACL pacl,
                                         CaseActionList caseActionList,
                                         DocumentContentRepository documentContentRepository,
                                         ReleasePackageCommentDocumentService releasePackageCommentDocumentService,
                                         ReleasePackageCommentService releasePackageCommentService, ReleasePackageDocumentRepository releasePackageDocumentRepository, DelegatingSecurityContextAsyncTaskExecutor executor) {
        super(stateMachine, abacProcessor, rbacProcessor, acl, pacl, caseActionList, documentContentRepository);
        this.releasePackageCommentDocumentService = releasePackageCommentDocumentService;
        this.releasePackageCommentService = releasePackageCommentService;
        this.releasePackageDocumentRepository = releasePackageDocumentRepository;
        this.executor = executor;
    }

    @Override
    public DocumentService getSelf() {
        return self;
    }


    public List<DocumentCategory> getAllCategorizedDocuments(EntityLink entityLink, String criteria, String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        CompletableFuture[] completableFutures = new CompletableFuture[2];
        List<DocumentOverview> allDocuments = new ArrayList<>();
        CompletableFuture<Void> commentDocumentsFuture = CompletableFuture.runAsync(() -> {
            List<Long> commentIds = releasePackageCommentService.getCommentIdsByParent(entityLink);
            commentIds.forEach(commentId -> {
                EntityLink commentEntityLink = new EntityLink(commentId, ReleasePackageComment.class);
                BaseEntityList<DocumentOverview> releasePackageCommentDocuments = releasePackageCommentDocumentService.getDocumentsOverview(criteria, viewCriteria, commentEntityLink, pageable, sliceSelect);
                allDocuments.addAll(releasePackageCommentDocuments.getResults());
            });
        },executor);
        CompletableFuture<Void> releasePackageDocumentsFuture = CompletableFuture.runAsync(() -> {
            BaseEntityList<DocumentOverview> releasePackageDocuments = getDocumentsOverview(criteria, viewCriteria, entityLink, pageable, sliceSelect);
            allDocuments.addAll(releasePackageDocuments.getResults());
        },executor);
        completableFutures[0] = commentDocumentsFuture;
        completableFutures[1] = releasePackageDocumentsFuture;
        CompletableFuture.allOf(completableFutures).join();

        BaseEntityList<DocumentOverview> as = new BaseEntityList<>();
        as.setResults(allDocuments);

        List<DocumentCategory> orderedCategoryList = new ArrayList<>();
        DocumentCategory lip = new DocumentCategory("LIP");
        DocumentCategory other = new DocumentCategory("OTHER");
        DocumentCategory note = new DocumentCategory("NOTE");
        ReleasePackage  releasePackage =  (ReleasePackage)releasePackageService.getEntityById(entityLink.getId());
        if (!releasePackage.getChangeOwnerType().equals("CREATOR")){
            orderedCategoryList.add(lip);
        }
        orderedCategoryList.add(other);
        orderedCategoryList.add(note);
        return groupDocumentsByCategory(as, Optional.of(orderedCategoryList));
    }

    public Integer getOtherDocumentsCountByReleasePackageId(Long releasePackageId) {
        return this.releasePackageDocumentRepository.getOtherDocumentsCount(releasePackageId);
    }
    public Integer getDocumentsCountByReleasePackageId(Long releasePackageId) {
        return this.releasePackageDocumentRepository.getCountByReleasePackageId(releasePackageId);
    }

}
