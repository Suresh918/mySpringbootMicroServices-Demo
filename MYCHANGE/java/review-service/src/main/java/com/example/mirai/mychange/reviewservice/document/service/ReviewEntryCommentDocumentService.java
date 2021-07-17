package com.example.mirai.projectname.reviewservice.document.service;

import com.example.mirai.libraries.core.annotation.EntityClass;
import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.document.repository.DocumentContentRepository;
import com.example.mirai.libraries.document.service.DocumentService;
import com.example.mirai.libraries.document.service.DocumentStateMachine;
import com.example.mirai.libraries.security.abac.component.AbacProcessor;
import com.example.mirai.libraries.security.core.component.CaseActionList;
import com.example.mirai.libraries.security.core.component.EntityACL;
import com.example.mirai.libraries.security.core.component.PropertyACL;
import com.example.mirai.libraries.security.rbac.component.RbacProcessor;
import com.example.mirai.projectname.reviewservice.document.model.ReviewEntryCommentDocument;
import com.example.mirai.projectname.reviewservice.document.repository.ReviewEntryCommentDocumentRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@EntityClass(ReviewEntryCommentDocument.class)
public class ReviewEntryCommentDocumentService extends DocumentService {
    @Resource
    ReviewEntryCommentDocumentService self;

    ReviewEntryCommentDocumentRepository reviewEntryCommentDocumentRepository;

    public ReviewEntryCommentDocumentService(DocumentStateMachine stateMachine, AbacProcessor abacProcessor,
                                             RbacProcessor rbacProcessor, EntityACL acl, PropertyACL pacl, CaseActionList caseActionList,
                                             DocumentContentRepository documentContentRepository,
                                             ReviewEntryCommentDocumentRepository reviewEntryCommentDocumentRepository) {
        super(stateMachine, abacProcessor, rbacProcessor, acl, pacl, caseActionList, documentContentRepository);
        this.reviewEntryCommentDocumentRepository = reviewEntryCommentDocumentRepository;
    }

    public List<Document> findDocumentsByCommentIdAndStatus(Long id, Integer statusCode) {
        return reviewEntryCommentDocumentRepository.findByReviewEntryCommentIdAndStatus(id, statusCode);
    }

    @Override
    public DocumentService getSelf() {
        return self;
    }

    public void delete(Long id) {
        super.delete(id);
    }
}
