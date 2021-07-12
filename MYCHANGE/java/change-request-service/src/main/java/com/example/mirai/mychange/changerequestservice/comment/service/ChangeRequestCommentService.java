package com.example.mirai.projectname.changerequestservice.comment.service;

import com.example.mirai.libraries.comment.model.Comment;
import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.comment.model.dto.CommentOverview;
import com.example.mirai.libraries.comment.service.CommentService;
import com.example.mirai.libraries.comment.service.CommentStateMachine;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.document.model.DocumentStatus;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.service.ChangeRequestService;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import com.example.mirai.projectname.changerequestservice.comment.repository.ChangeRequestCommentRepository;
import com.example.mirai.projectname.changerequestservice.core.component.AuthenticatedContext;
import com.example.mirai.projectname.changerequestservice.document.service.ChangeRequestCommentDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;

@Service
@EntityClass(ChangeRequestComment.class)
public class ChangeRequestCommentService extends CommentService {

    private ChangeRequestCommentRepository changeRequestCommentRepository;
    private AuthenticatedContext authenticatedContext;
    private ChangeRequestCommentDocumentService changeRequestCommentDocumentService;
    @Autowired
    private ChangeRequestService changeRequestService;

    @Autowired
    public ChangeRequestCommentService(ChangeRequestCommentDocumentService documentService,
                                        ChangeRequestCommentRepository changeRequestCommentRepository,
                                        AuthenticatedContext authenticatedContext,
                                        ChangeRequestCommentDocumentService changeRequestCommentDocumentService,
                                        CommentStateMachine stateMachine, AbacProcessor abacProcessor,
                                        RbacProcessor rbacProcessor, EntityACL acl, PropertyACL pacl,
                                        CaseActionList caseActionList) {

        super(documentService, stateMachine, abacProcessor, rbacProcessor, acl, pacl, caseActionList);
        this.changeRequestCommentRepository = changeRequestCommentRepository;
        this.authenticatedContext = authenticatedContext;
        this.changeRequestCommentDocumentService = changeRequestCommentDocumentService;
    }

    @Transactional
    public Comment createCommentMigrate(BaseEntityInterface entity, Long parentId, Class parentEntityClass, Date createdOn, User creator) throws ParseException {
        Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet();
        entityLinkSet.add(new EntityLink(parentId, parentEntityClass));
        ChangeRequestComment changeRequestComment = (ChangeRequestComment) this.createLinkedEntityWithLinks(entity, entityLinkSet);
        changeRequestComment.setCreatedOn(createdOn);
        changeRequestComment.setCreator(creator);
        super.update(changeRequestComment);
        return changeRequestComment;
    }

    public ChangeRequestComment findFirstUnremovedCommentByReplyToId(Long commentId) {
        return changeRequestCommentRepository.findFirstByReplyToIdAndStatusNot(commentId, CommentStatus.REMOVED.getStatusCode());
    }

    public Integer getCommentsCountByChangeRequestIdAndAuditor(Long changeRequestId) {
        User auditor = authenticatedContext.getAuditableUser();
        return changeRequestCommentRepository.getCommentsCountByChangeRequestIdAndAuditor(changeRequestId, auditor.getUserId());
    }

    @Override
    public BaseEntityList<CommentOverview> getCommentsOverviewByParent(String criteria, Class<? extends BaseEntityInterface> parentEntityClass, EntityLink entityLink, Pageable pageable, Optional<String> sliceSelect) {
        BaseEntityList<CommentOverview> commentOverviewList = super.getCommentsOverviewByParent(criteria, parentEntityClass, entityLink, pageable, sliceSelect);
        commentOverviewList.getResults().stream().forEach(commentOverview -> {
            Long commentId = commentOverview.getId();
            commentOverview.setDocuments(changeRequestCommentDocumentService.findDocumentsByCommentIdAndStatus(commentId, DocumentStatus.PUBLISHED.getStatusCode()));
        });
        return commentOverviewList;
    }

    public List<ChangeRequestComment> getCommentsByChangeRequestId(Long changeRequestId) {
        List<ChangeRequestComment> changeRequestComments = (List<ChangeRequestComment>) filter("changeRequest.id:" + changeRequestId, PageRequest.of(0, Integer.MAX_VALUE - 1));
        return changeRequestComments;
    }


    public BaseEntityList<CommentOverview> getChangeRequestCommentOverviewByAgendaItemId(String agendaItemId, String criteria, Pageable pageable, Optional<String> sliceSelect) {
        Long changeRequestId = changeRequestService.getChangeRequestIdByAgendaItemId(agendaItemId);
        if (Objects.nonNull(changeRequestId)) {
            EntityLink entityLink = new EntityLink<BaseEntityInterface>(changeRequestId, (Class) ChangeRequest.class);
            return getCommentsOverviewByParent(criteria, ChangeRequest.class, entityLink, pageable, sliceSelect);
        }
        return null;
    }

}
