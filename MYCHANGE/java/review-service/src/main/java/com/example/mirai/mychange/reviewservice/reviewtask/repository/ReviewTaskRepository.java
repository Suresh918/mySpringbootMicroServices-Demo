package com.example.mirai.projectname.reviewservice.reviewtask.repository;


import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.util.List;

public interface ReviewTaskRepository extends JpaRepository<ReviewTask, Long>, JpaSpecificationExecutor<ReviewTask>
        , BaseRepository<ReviewTask> {
    List<ReviewTask> findByReviewAndAssignee_UserId(Review review, String userId);

    List<ReviewTask> findReviewTasksByStatusInAndDueDateLessThan(List<Integer> statuses, Timestamp day);

    List<ReviewTask> findReviewTasksByStatusInAndDueDateBetween(List<Integer> statuses, Timestamp startDay, Timestamp endDay);

    @Query("SELECT re FROM ReviewTask re WHERE review_id=?1 and user_id=?2")
    List<ReviewTask> findReviewTasksByReviewIdAndAssigneeUserId(Long reviewId, String assigneeUserId);

    @Query("SELECT re FROM ReviewTask re WHERE review_id=?1 and status IN ?2")
    List<ReviewTask> findReviewTasksByReviewIdAndInStatuses(Long id, Integer[] statuses);
}
