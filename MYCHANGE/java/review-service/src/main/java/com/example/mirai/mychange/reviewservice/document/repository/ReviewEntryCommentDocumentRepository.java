package com.example.mirai.projectname.reviewservice.document.repository;

import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.reviewservice.document.model.ReviewEntryCommentDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ReviewEntryCommentDocumentRepository extends JpaRepository<ReviewEntryCommentDocument, Long>,
        JpaSpecificationExecutor<ReviewEntryCommentDocument>,
        BaseRepository<ReviewEntryCommentDocumentRepository> {

    List<Document> findByReviewEntryCommentIdAndStatus(Long commentId, Integer status);
}
