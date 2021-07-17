package com.example.mirai.projectname.releasepackageservice.document.service;

import java.util.List;

import javax.annotation.Resource;

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
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageCommentDocument;
import com.example.mirai.projectname.releasepackageservice.document.repository.ReleasePackageCommentDocumentRepository;

import org.springframework.stereotype.Service;

@Service
@EntityClass(ReleasePackageCommentDocument.class)
public class ReleasePackageCommentDocumentService extends DocumentService {
    private final ReleasePackageCommentDocumentRepository releasePackageCommentDocumentRepository;
    @Resource
    ReleasePackageCommentDocumentService self;

    public ReleasePackageCommentDocumentService(DocumentStateMachine stateMachine, AbacProcessor abacProcessor,
                                                RbacProcessor rbacProcessor, EntityACL acl, PropertyACL pacl,
                                                CaseActionList caseActionList,
                                                DocumentContentRepository documentContentRepository,
                                                ReleasePackageCommentDocumentRepository releasePackageCommentDocumentRepository) {
        super(stateMachine, abacProcessor, rbacProcessor, acl, pacl, caseActionList, documentContentRepository);
        this.releasePackageCommentDocumentRepository = releasePackageCommentDocumentRepository;
    }

    @Override
    public DocumentService getSelf() {
        return self;
    }


    public List<Document> findDocumentsByCommentIdAndStatus(Long id, Integer statusCode) {
        return releasePackageCommentDocumentRepository.findByReleasePackageCommentIdAndStatus(id, statusCode);
    }
    public Integer getDocumentsCountByReleasePackageCommentIds(List<Long> commentIds) {
        return this.releasePackageCommentDocumentRepository.getCountByReleasePackageCommentIds(commentIds);
    }

}
