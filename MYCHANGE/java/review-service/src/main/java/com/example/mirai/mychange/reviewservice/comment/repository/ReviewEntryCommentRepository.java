package com.example.mirai.projectname.reviewservice.comment.repository;

import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.reviewservice.comment.model.ReviewEntryComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ReviewEntryCommentRepository extends JpaRepository<ReviewEntryComment, Long>, JpaSpecificationExecutor<ReviewEntryComment>,
        BaseRepository<ReviewEntryComment> {
    ReviewEntryComment findFirstByReplyToIdAndStatusNot(Long commentId, Integer status);

    @Query("SELECT COUNT(c.id) FROM ReviewEntryComment c where reviewentry_id=?1 and (status=2 or (status=1 and creator_user_id=?2))")
    Integer getCommentsCountByReviewEntryIdAndAuditor(Long reviewEntryId, String auditorUserId);
}
