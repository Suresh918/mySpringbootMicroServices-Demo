package com.example.mirai.projectname.reviewservice.reviewentry.repository;

import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewEntryRepository extends JpaRepository<ReviewEntry, Long>, JpaSpecificationExecutor<ReviewEntry>,
        BaseRepository<ReviewEntry> {

    List<ReviewEntry> findReviewEntriesByReviewTask(ReviewTask reviewTask, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT MAX(re.sequenceNumber) FROM ReviewEntry re WHERE review_id=?1")
    List<Integer> findByReviewAndOrderBySequenceNumberDesc(Long reviewId);

    @Query("SELECT re.status FROM ReviewEntry re WHERE review_id=?1")
    List<Integer> findReviewEntriesStatusByReviewId(Long reviewId);
}
