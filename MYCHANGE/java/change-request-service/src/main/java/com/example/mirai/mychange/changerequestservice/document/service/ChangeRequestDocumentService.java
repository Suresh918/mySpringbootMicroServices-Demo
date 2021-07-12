package com.example.mirai.projectname.changerequestservice.document.service;

import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.BaseView;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.document.model.Document;
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
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeOwnerType;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.comment.service.ChangeRequestCommentService;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestDocument;
import com.example.mirai.projectname.changerequestservice.document.repository.ChangeRequestDocumentRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@EntityClass(ChangeRequestDocument.class)
public class ChangeRequestDocumentService extends DocumentService {

    private final ChangeRequestCommentDocumentService changeRequestCommentDocumentService;
    private final ChangeRequestDocumentRepository changeRequestDocumentRepository;
    private final ChangeRequestCommentService changeRequestCommentService;
    private final ChangeRequestService changeRequestService;
    private final  DelegatingSecurityContextAsyncTaskExecutor executor;
    @Resource
    private ChangeRequestDocumentService self;

    public ChangeRequestDocumentService(DocumentStateMachine stateMachine, AbacProcessor abacProcessor,
                                        RbacProcessor rbacProcessor, EntityACL acl, PropertyACL pacl,
                                        DocumentContentRepository documentContentRepository, ChangeRequestDocumentRepository changeRequestDocumentRepository,
                                        CaseActionList caseActionList, ChangeRequestCommentDocumentService changeRequestCommentDocumentService, ChangeRequestCommentService changeRequestCommentService,
                                        ChangeRequestService changeRequestService, DelegatingSecurityContextAsyncTaskExecutor executor) {
        super(stateMachine, abacProcessor, rbacProcessor, acl, pacl, caseActionList, documentContentRepository);
        this.changeRequestCommentDocumentService = changeRequestCommentDocumentService;
        this.changeRequestDocumentRepository = changeRequestDocumentRepository;
        this.changeRequestCommentService = changeRequestCommentService;
        this.changeRequestService = changeRequestService;
        this.executor = executor;
    }

    @Override
    public DocumentService getSelf() {
        return self;
    }

    public void delete(Long id) {
        super.delete(id);
    }

    public Document createMigrationDocument(BaseEntityInterface entity, Long parentId, Class parentEntityClass, MultipartFile file) throws IOException {
        return super.createMigrationDocument(entity, parentId, parentEntityClass, file);
    }

    public BaseEntityList<BaseView> getDocumentsOverview(EntityLink entityLink, String criteria, String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        if (!Objects.isNull(criteria) && criteria.length() > 0)
            criteria = "(changeRequest.id:" + entityLink.getId() + ") and (" + criteria + ")";
        else
            criteria = "(changeRequest.id:" + entityLink.getId() + ")";

        Slice<BaseView> changeRequestDocumentOverviewSlice = this.getEntitiesFromView(criteria,viewCriteria, pageable, sliceSelect, DocumentOverview.class);
        return new BaseEntityList(changeRequestDocumentOverviewSlice);
    }

    public Integer getDocumentsCountByChangeRequestId(Long changeRequestId) {
        return this.changeRequestDocumentRepository.getCountByChangeRequestId(changeRequestId);
    }

    public Integer getDocumentsCountByChangeRequestCommentIds(List<Long> commentIds) {
        return this.changeRequestDocumentRepository.getCountByChangeRequestCommentIds(commentIds);
    }

    public Integer getOtherDocumentsCountByChangeRequestCommentIds(Long changeRequestId) {
        return this.changeRequestDocumentRepository.getOtherDocumentsCount(changeRequestId);
    }

    public List<DocumentCategory> getAllCategorizedDocuments(EntityLink entityLink, String criteria, String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        CompletableFuture[] completableFutures = new CompletableFuture[2];
        List<DocumentOverview> allDocuments = new ArrayList<>();
        CompletableFuture<Void> commentDocumentsFuture = CompletableFuture.runAsync(() -> {
            List<Long> commentIds = changeRequestCommentService.getCommentIdsByParent(entityLink);
            commentIds.forEach(commentId -> {
                EntityLink commentEntityLink = new EntityLink(commentId, ChangeRequestComment.class);
                BaseEntityList<DocumentOverview> changeRequestCommentDocuments = changeRequestCommentDocumentService.getDocumentsOverview(criteria, viewCriteria, commentEntityLink, pageable, sliceSelect);
                allDocuments.addAll(changeRequestCommentDocuments.getResults());
            });
        }, executor);
        CompletableFuture<Void> changeRequestDocumentsFuture = CompletableFuture.runAsync(() -> {
            BaseEntityList<DocumentOverview> changeRequestDocuments = getDocumentsOverview(criteria, viewCriteria, entityLink, pageable, sliceSelect);
            allDocuments.addAll(changeRequestDocuments.getResults());
        }, executor);
        completableFutures[0] = commentDocumentsFuture;
        completableFutures[1] = changeRequestDocumentsFuture;
        CompletableFuture.allOf(completableFutures).join();

        BaseEntityList<DocumentOverview> documentOverviewBaseEntityList = new BaseEntityList<>();
        documentOverviewBaseEntityList.setResults(allDocuments);

        List<DocumentCategory> orderedCategoryList = new ArrayList<>();
        DocumentCategory scia = new DocumentCategory("SCIA");
        DocumentCategory cbc = new DocumentCategory("CBC");
        DocumentCategory other = new DocumentCategory("OTHER");
        DocumentCategory note = new DocumentCategory("NOTE");
        DocumentCategory asIsPicture = new DocumentCategory("AS-IS-PICTURE");
        DocumentCategory toBePicture = new DocumentCategory("TO-BE-PICTURE");

        ChangeRequest changeRequest = (ChangeRequest) changeRequestService.getEntityById(entityLink.getId());
        if (Objects.equals(ChangeOwnerType.PROJECT.name(), changeRequest.getChangeOwnerType())) {
            orderedCategoryList.add(scia);
            orderedCategoryList.add(cbc);
        }
        orderedCategoryList.add(other);
        orderedCategoryList.add(note);
        if (Objects.equals(changeRequest.getChangeOwnerType(), ChangeOwnerType.PROJECT.name())) {
            orderedCategoryList.add(asIsPicture);
            orderedCategoryList.add(toBePicture);
        }

        return groupDocumentsByCategory(documentOverviewBaseEntityList, Optional.of(orderedCategoryList));
    }

    public List<DocumentCategory> getAllDocumentsOverviewByAgendaItemId(String agendaItemId, String criteria, String viewCriteria, Pageable pageable, Optional<String> sliceSelect) {
        Long changeRequestId = changeRequestService.getChangeRequestIdByAgendaItemId(agendaItemId);
        if (Objects.nonNull(changeRequestId)) {
            EntityLink entityLink = new EntityLink<BaseEntityInterface>(changeRequestId, (Class) ChangeRequest.class);
            return getAllCategorizedDocuments(entityLink, criteria, viewCriteria, pageable, sliceSelect);
        }
        return new ArrayList<>();
    }
}
