package com.example.mirai.projectname.changerequestservice.document.repository;

import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.changerequestservice.document.model.ChangeRequestDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChangeRequestDocumentRepository extends JpaRepository<ChangeRequestDocument, Long>,
        JpaSpecificationExecutor<ChangeRequestDocument>,
        BaseRepository<ChangeRequestDocumentRepository> {
    @Query("SELECT COUNT(d.id) FROM ChangeRequestDocument d WHERE change_request_id=?1")
    Integer getCountByChangeRequestId(Long changeRequestId);
    @Query("SELECT COUNT(d.id) FROM ChangeRequestCommentDocument d WHERE change_request_comment_id IN ?1")
    Integer getCountByChangeRequestCommentIds(List<Long> changeRequestCommentId);

    @Query("SELECT COUNT(d.id) FROM ChangeRequestDocument d WHERE change_request_id =?1 and 'OTHER' MEMBER OF tags")
    Integer getOtherDocumentsCount(Long changeRequestId);
}
