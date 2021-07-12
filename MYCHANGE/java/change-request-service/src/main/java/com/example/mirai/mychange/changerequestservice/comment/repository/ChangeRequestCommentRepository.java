package com.example.mirai.projectname.changerequestservice.comment.repository;

import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.changerequestservice.comment.model.ChangeRequestComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ChangeRequestCommentRepository extends JpaRepository<ChangeRequestComment, Long>, JpaSpecificationExecutor<ChangeRequestComment>,
        BaseRepository<ChangeRequestComment> {
    ChangeRequestComment findFirstByReplyToIdAndStatusNot(Long commentId, Integer status);
    @Query("SELECT COUNT(c.id) FROM ChangeRequestComment c WHERE change_request_id=?1 and (status=2 OR (status=1 and creator_user_id=?2))")
    Integer getCommentsCountByChangeRequestIdAndAuditor(Long changeRequestId, String auditorUserId);
}
