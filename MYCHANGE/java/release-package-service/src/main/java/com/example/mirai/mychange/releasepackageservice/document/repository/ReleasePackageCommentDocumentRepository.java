package com.example.mirai.projectname.releasepackageservice.document.repository;

import java.util.List;

import com.example.mirai.libraries.document.model.Document;
import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageCommentDocument;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ReleasePackageCommentDocumentRepository extends JpaRepository<ReleasePackageCommentDocument, Long>,
        JpaSpecificationExecutor<ReleasePackageCommentDocument>,
        BaseRepository<ReleasePackageCommentDocumentRepository> {

    List<Document> findByReleasePackageCommentIdAndStatus(Long commentId, Integer status);

    @Query("SELECT COUNT(d.id) FROM ReleasePackageCommentDocument d WHERE release_package_comment_id IN ?1")
    Integer getCountByReleasePackageCommentIds(List<Long> releasePackageCommentId);

}
