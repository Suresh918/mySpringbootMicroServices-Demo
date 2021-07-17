package com.example.mirai.projectname.reviewservice.review.repository;

import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.dto.ReviewEntryContextCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long>,
        JpaSpecificationExecutor<Review>, BaseRepository<Review> {
    @Query("SELECT r FROM Review r INNER JOIN r.contexts c WHERE c.contextId = ?1 AND c.type = ?2")
    List<Review> findAllReviewsByContextIdAndContextType(String contextId, String contextType);

    @Query("select rec.contextId as id, count(re.id) as count\n" +
            "from ReviewEntry re\n" +
            "join re.contexts rec \n" +
            "where re.review.id =?1 and rec.type =?2 group by rec.contextId")
    List<ReviewEntryContextCount> findReviewEntryContextCountByReviewId(Long reviewId, String contextStr);

}
