package com.example.mirai.projectname.changerequestservice.document.repository;

import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestCommentDocument;
import com.example.mirai.libraries.document.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ChangeRequestCommentDocumentRepository extends JpaRepository<ChangeRequestCommentDocument, Long>,
        JpaSpecificationExecutor<ChangeRequestCommentDocument>,
        BaseRepository<ChangeRequestCommentDocumentRepository> {

    List<Document> findByChangeRequestCommentIdAndStatus(Long commentId, Integer status);
}
