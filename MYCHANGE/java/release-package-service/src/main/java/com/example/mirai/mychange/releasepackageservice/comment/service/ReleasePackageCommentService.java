package com.example.mirai.projectname.releasepackageservice.comment.service;

import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.example.mirai.libraries.comment.model.Comment;
import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.comment.model.dto.CommentOverview;
import com.example.mirai.libraries.comment.service.CommentService;
import com.example.mirai.libraries.comment.service.CommentStateMachine;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.document.model.DocumentStatus;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.comment.repository.ReleasePackageCommentRepository;
import com.example.mirai.projectname.releasepackageservice.core.component.AuthenticatedContext;
import com.example.mirai.projectname.releasepackageservice.document.service.ReleasePackageCommentDocumentService;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@EntityClass(ReleasePackageComment.class)
public class ReleasePackageCommentService extends CommentService {

    private ReleasePackageCommentRepository releasePackageCommentRepository;
    private AuthenticatedContext authenticatedContext;
    private ReleasePackageCommentDocumentService releasePackageCommentDocumentService;

    public ReleasePackageCommentService(ReleasePackageCommentDocumentService documentService,
                                        ReleasePackageCommentRepository releasePackageCommentRepository,
                                        AuthenticatedContext authenticatedContext,
                                        ReleasePackageCommentDocumentService releasePackageCommentDocumentService,
                                        CommentStateMachine stateMachine, AbacProcessor abacProcessor,
                                        RbacProcessor rbacProcessor, EntityACL acl, PropertyACL pacl,
                                        CaseActionList caseActionList) {

        super(documentService, stateMachine, abacProcessor, rbacProcessor, acl, pacl, caseActionList);
        this.releasePackageCommentRepository = releasePackageCommentRepository;
        this.authenticatedContext = authenticatedContext;
        this.releasePackageCommentDocumentService=releasePackageCommentDocumentService;
    }

    @Transactional
    public Comment createCommentMigrate(BaseEntityInterface entity, Long parentId, Class parentEntityClass, Date createdOn, User creator) throws ParseException {
        Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet();
        entityLinkSet.add(new EntityLink(parentId, parentEntityClass));
        ReleasePackageComment releasePackageComment = (ReleasePackageComment) this.createLinkedEntityWithLinks(entity, entityLinkSet);
        releasePackageComment.setCreatedOn(createdOn);
        releasePackageComment.setCreator(creator);
        super.update(releasePackageComment);
        return releasePackageComment;
    }

    public ReleasePackageComment findFirstUnremovedCommentByReplyToId(Long commentId) {
        return releasePackageCommentRepository.findFirstByReplyToIdAndStatusNot(commentId, CommentStatus.REMOVED.getStatusCode());
    }

    public Integer getCommentsCountByReleasePackageIdAndAuditor(Long releasePackageId) {
        User auditor = authenticatedContext.getAuditableUser();
        return releasePackageCommentRepository.getCommentsCountByReleasePackageIdAndAuditor(releasePackageId, auditor.getUserId());
    }

    @Override
    public BaseEntityList<CommentOverview> getCommentsOverviewByParent(String criteria, Class<? extends BaseEntityInterface> parentEntityClass, EntityLink entityLink, Pageable pageable, Optional<String> sliceSelect) {
        BaseEntityList<CommentOverview> commentOverviewList = super.getCommentsOverviewByParent(criteria, parentEntityClass, entityLink, pageable, sliceSelect);
        commentOverviewList.getResults().stream().forEach(commentOverview -> {
            Long commentId = commentOverview.getId();
            commentOverview.setDocuments(releasePackageCommentDocumentService.findDocumentsByCommentIdAndStatus(commentId, DocumentStatus.PUBLISHED.getStatusCode()));
        });
        return commentOverviewList;
    }

    @Override
    public void delete(Long id) {
        List<Document> documents = ((ReleasePackageCommentDocumentService) documentService).findDocumentsByCommentIdAndStatus(id, DocumentStatus.PUBLISHED.getStatusCode());
        super.delete(id, documents);
    }

    @SecureCaseAction("UPDATE")
    @Transactional
    @Override
    public BaseEntityInterface update(BaseEntityInterface entity,  Map<String, Object> newInsChangedAttrs){
        return super.update(entity, newInsChangedAttrs);
   }

}
