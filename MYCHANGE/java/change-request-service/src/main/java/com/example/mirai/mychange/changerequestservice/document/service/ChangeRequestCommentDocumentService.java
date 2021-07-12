package com.example.mirai.projectname.changerequestservice.document.service;

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
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestCommentDocument;
import com.example.mirai.projectname.changerequestservice.document.repository.ChangeRequestCommentDocumentRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@EntityClass(ChangeRequestCommentDocument.class)
public class ChangeRequestCommentDocumentService extends DocumentService {
    private final ChangeRequestCommentDocumentRepository changeRequestCommentDocumentRepository;
    @Resource
    ChangeRequestCommentDocumentService self;

    public ChangeRequestCommentDocumentService(DocumentStateMachine stateMachine, AbacProcessor abacProcessor,
                                               RbacProcessor rbacProcessor, EntityACL acl, PropertyACL pacl,
                                               CaseActionList caseActionList,
                                               DocumentContentRepository documentContentRepository,
                                               ChangeRequestCommentDocumentRepository changeRequestCommentDocumentRepository) {
        super(stateMachine, abacProcessor, rbacProcessor, acl, pacl, caseActionList, documentContentRepository);
        this.changeRequestCommentDocumentRepository = changeRequestCommentDocumentRepository;
    }

    @Override
    public DocumentService getSelf() {
        return self;
    }


    public List<Document> findDocumentsByCommentIdAndStatus(Long id, Integer statusCode) {
        return changeRequestCommentDocumentRepository.findByChangeRequestCommentIdAndStatus(id, statusCode);
    }
    /*public Integer getDocumentsCountByChangeRequestCommentIds(List<Long> commentIds) {
        return this.changeRequestCommentDocumentRepository.getCountByChangeRequestCommentIds(commentIds);
    }*/

}
