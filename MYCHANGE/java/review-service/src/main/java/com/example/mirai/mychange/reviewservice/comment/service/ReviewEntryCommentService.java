package com.example.mirai.projectname.reviewservice.comment.service;

import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.comment.model.dto.CommentOverview;
import com.example.mirai.libraries.comment.service.CommentService;
import com.example.mirai.libraries.comment.service.CommentStateMachine;
import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.BaseEntityList;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.document.model.DocumentStatus;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.projectname.reviewservice.comment.model.ReviewEntryComment;
import com.example.mirai.projectname.reviewservice.comment.repository.ReviewEntryCommentRepository;
import com.example.mirai.projectname.reviewservice.core.component.AuthenticatedContext;
import com.example.mirai.projectname.reviewservice.document.service.ReviewEntryCommentDocumentService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Service
@EntityClass(ReviewEntryComment.class)
public class ReviewEntryCommentService extends CommentService {

    @Resource
    CommentService commentService;
    private final ReviewEntryCommentRepository reviewEntryCommentRepository;
    private final ReviewEntryCommentDocumentService reviewEntryCommentDocumentService;
    private final AuthenticatedContext auditorExtractorImpl;

    public ReviewEntryCommentService(ReviewEntryCommentDocumentService reviewEntryCommentDocumentService, CommentStateMachine stateMachine, AbacProcessor abacProcessor,
                                     RbacProcessor rbacProcessor, EntityACL acl, PropertyACL pacl, CaseActionList caseActionList,
                                     AuthenticatedContext auditorExtractorImpl, ReviewEntryCommentRepository reviewEntryCommentRepository, ReviewEntryCommentDocumentService reviewEntryCommentDocumentService1) {
        super(reviewEntryCommentDocumentService, stateMachine, abacProcessor, rbacProcessor, acl, pacl, caseActionList);
        this.auditorExtractorImpl = auditorExtractorImpl;
        this.reviewEntryCommentRepository = reviewEntryCommentRepository;
        this.reviewEntryCommentDocumentService = reviewEntryCommentDocumentService1;
    }

    private ReviewEntryCommentDocumentService getDocumentService() {
        return (ReviewEntryCommentDocumentService) super.documentService;
    }

    public ReviewEntryComment findFirstUnremovedCommentByReplyToId(Long commentId) {
        return reviewEntryCommentRepository.findFirstByReplyToIdAndStatusNot(commentId, CommentStatus.REMOVED.getStatusCode());
    }

    @Override
    public void delete(Long id) {
        BaseEntityInterface comment = this.getById(id);
        List<Document> documents = getDocumentService().findDocumentsByCommentIdAndStatus(id, DocumentStatus.PUBLISHED.getStatusCode());
        super.delete(id, documents);
    }

    public Integer getCommentsCountByReviewEntryIdAndAuditor(Long reviewEntryId, String userId) {
        return reviewEntryCommentRepository.getCommentsCountByReviewEntryIdAndAuditor(reviewEntryId, userId);
    }

    @Override
    public ReviewEntryComment remove(BaseEntityInterface entity) {
        List<Document> documents = getDocumentService().findDocumentsByCommentIdAndStatus(entity.getId(), DocumentStatus.PUBLISHED.getStatusCode());
        return (ReviewEntryComment) super.remove(entity, documents);
    }

    @Override
    public BaseEntityList<CommentOverview> getCommentsOverviewByParent(String criteria, Class<? extends BaseEntityInterface> parentEntityClass, EntityLink entityLink, Pageable pageable, Optional<String> sliceSelect) {
        BaseEntityList<CommentOverview> commentOverviewList = super.getCommentsOverviewByParent(criteria, parentEntityClass, entityLink, pageable, sliceSelect);
        commentOverviewList.getResults().stream().forEach(commentOverview -> {
            Long commentId = commentOverview.getId();
            commentOverview.setDocuments(reviewEntryCommentDocumentService.findDocumentsByCommentIdAndStatus(commentId, DocumentStatus.PUBLISHED.getStatusCode()));
        });
        return commentOverviewList;
    }
}
