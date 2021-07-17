package com.example.mirai.projectname.releasepackageservice.comment.repository;


import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ReleasePackageCommentRepository extends JpaRepository<ReleasePackageComment, Long>, JpaSpecificationExecutor<ReleasePackageComment>,
        BaseRepository<ReleasePackageComment> {
    ReleasePackageComment findFirstByReplyToIdAndStatusNot(Long commentId, Integer status);

    @Query(value = "SELECT COUNT(c.id) FROM ReleasePackageComment c where release_package_id=?1 and (status=2 or (status=1 and creator_user_id=?2))")
    Integer getCommentsCountByReleasePackageIdAndAuditor(Long releasePackageId, String auditorUserId);
}
