package com.example.mirai.projectname.reviewservice.fixtures;


import com.example.mirai.projectname.reviewservice.review.model.Review;
import com.example.mirai.projectname.reviewservice.review.model.ReviewContext;
import com.example.mirai.projectname.reviewservice.review.model.ReviewStatus;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntry;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryContext;
import com.example.mirai.projectname.reviewservice.reviewentry.model.ReviewEntryStatus;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTask;
import com.example.mirai.projectname.reviewservice.reviewtask.model.ReviewTaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class EntityInstanceManager {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long findReviewIdByTitle(String title) {
        String sql = "SELECT ID FROM REVIEW WHERE TITLE = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{title}, (rs, rowNum) ->
                rs.getLong("id"));

    }

    public Long findReviewTaskIdByAssignee(String userId) {
        String sql = "SELECT ID FROM REVIEW_TASK WHERE USER_ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{userId}, (rs, rowNum) ->
                rs.getLong("id"));

    }

    public Long findReviewTaskIdByDueDate(Date dueDate) {
        String sql = "SELECT ID FROM REVIEW_TASK WHERE DUE_DATE = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{dueDate}, (rs, rowNum) ->
                rs.getLong("id"));

    }

    public Long findReviewEntryIdByReviewTaskId(Long reviewId, Long reviewTaskId) {
        String sql = "SELECT ID FROM REVIEW_ENTRY WHERE review_id = ? and reviewtask_id=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{reviewId, reviewTaskId}, (rs, rowNum) ->
                rs.getLong("id"));

    }


    public Long getNextHibernateSequence() {
        String sql = "select nextval ('hibernate_sequence') as nextval";
        return jdbcTemplate.queryForObject(sql, new Object[]{}, (rs, rowNum) ->
                rs.getLong("nextval"));

    }


    public Long createReview(String dataIdentifier, String properties) {
        Review requestReview = null;
        requestReview = EntityPojoFactory.createReview(dataIdentifier, properties);


        String stmt = "INSERT INTO public.review(" +
                "completion_date, created_on, status, title, " +
                "creator_abbreviation, creator_department_name, creator_email, creator_full_name, creator_user_id, " +
                "executor_abbreviation, executor_department_name, executor_email, executor_full_name, executor_user_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt,
                requestReview.getCompletionDate(), requestReview.getCreatedOn(), requestReview.getStatus(), requestReview.getTitle(),
                requestReview.getCreator().getAbbreviation(), requestReview.getCreator().getDepartmentName(), requestReview.getCreator().getEmail(), requestReview.getCreator().getFullName(), requestReview.getCreator().getUserId(),
                requestReview.getExecutor().getAbbreviation(), requestReview.getExecutor().getDepartmentName(), requestReview.getExecutor().getEmail(), requestReview.getExecutor().getFullName(), requestReview.getExecutor().getUserId()
        );

        Long reviewId = findReviewIdByTitle(requestReview.getTitle());

        int order = 0;
        for (ReviewContext reviewContext : requestReview.getContexts()) {
            stmt = "INSERT INTO review_contexts(" +
                    "id, context_id, name, status, type, contexts_order) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(stmt,
                    reviewId, reviewContext.getContextId(), reviewContext.getName(), reviewContext.getStatus(), reviewContext.getType(), order++);
        }

        Long nextVal = getNextHibernateSequence();
        stmt = "insert into aud_updater (abbreviation, department_name, email, full_name, " +
                "timestamp, user_id, id) values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt,
                requestReview.getCreator().getAbbreviation(), requestReview.getCreator().getDepartmentName(), requestReview.getCreator().getEmail(), requestReview.getCreator().getFullName(),
                (new Date()).getTime(), requestReview.getCreator().getUserId(), nextVal);


        stmt = "insert into aud_review (revtype, revend, revend_tstmp, " +
                "completion_date, completion_date_mod, created_on, created_on_mod, " +
                "creator_abbreviation, creator_department_name, creator_email, creator_full_name, creator_user_id, creator_mod, " +
                "executor_abbreviation, executor_department_name, executor_email, executor_full_name, executor_user_id, executor_mod, " +
                "status, status_mod, title, title_mod, contexts_mod, id, rev) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(stmt, 1, null, null,
                requestReview.getCompletionDate(), false, requestReview.getCreatedOn(), false,
                requestReview.getCreator().getAbbreviation(), requestReview.getCreator().getDepartmentName(), requestReview.getCreator().getEmail(), requestReview.getCreator().getFullName(), requestReview.getCreator().getUserId(), false,
                requestReview.getExecutor().getAbbreviation(), requestReview.getExecutor().getDepartmentName(), requestReview.getExecutor().getEmail(), requestReview.getExecutor().getFullName(), requestReview.getExecutor().getUserId(), false,
                requestReview.getStatus(), false, requestReview.getTitle(), false, false, reviewId, nextVal);

        order = 0;
        for (ReviewContext reviewContext : requestReview.getContexts()) {
            stmt = "insert into aud_review_contexts (revend, revend_tstmp," +
                    " name, context_id, type, status, " +
                    "rev, revtype, id, contexts_order) " +
                    "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(stmt,
                    null, null,
                    reviewContext.getName(), reviewContext.getContextId(), reviewContext.getType().toUpperCase(), reviewContext.getStatus(),
                    nextVal, 0, reviewId, order++);
        }

        return reviewId;
    }

    public Long createReviewAndSetStatus(String dataIdentifier, String properties, ReviewStatus status) {
        Long id = createReview(dataIdentifier, properties);
        assert id != null;
        String stmt = "update review set status = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, status.getStatusCode(), id);
        assert updatedRecords == 1;

        return id;
    }

    public void setReleasePackageStatus(Long reviewId, String releasePackageStatus) {
        String stmt = "update review_contexts set status = ? where id = ? and type = 'RELEASEPACKAGE'";
        int updatedRecords = jdbcTemplate.update(stmt, releasePackageStatus, reviewId);
        assert updatedRecords == 1;
    }

    public void setReviewEntryStatus(Long reviewEntryId, Integer status) throws Exception {
        String stmt = "update review_entry set status = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, status, reviewEntryId);
        assert updatedRecords == 1;
    }

    public Long createReviewTask(String dataIdentifier, String properties, Long reviewId) {
        ReviewTask requestReviewTask = null;
        requestReviewTask = EntityPojoFactory.createReviewTask(dataIdentifier, properties);
        requestReviewTask.setReview(new Review());
        requestReviewTask.getReview().setId(reviewId);
        String stmt = "INSERT INTO public.review_task(" +
                "due_date, abbreviation, department_name, email, full_name, user_id, created_on," +
                "creator_abbreviation, creator_department_name, creator_email, creator_full_name, creator_user_id, " +
                "status, review_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        String assigneeAbbreviation = requestReviewTask.getAssignee() != null ? requestReviewTask.getAssignee().getAbbreviation() : null;
        String assigneeDepartmentName = requestReviewTask.getAssignee() != null ? requestReviewTask.getAssignee().getDepartmentName() : null;
        String assigneeEmail = requestReviewTask.getAssignee() != null ? requestReviewTask.getAssignee().getEmail() : null;
        String assigneeFullName = requestReviewTask.getAssignee() != null ? requestReviewTask.getAssignee().getFullName() : null;
        String assigneeUserId = requestReviewTask.getAssignee() != null ? requestReviewTask.getAssignee().getUserId() : null;

        String creatorAbbreviation = requestReviewTask.getCreator() != null ? requestReviewTask.getCreator().getAbbreviation() : null;
        String creatorDepartmentName = requestReviewTask.getCreator() != null ? requestReviewTask.getCreator().getDepartmentName() : null;
        String creatorEmail = requestReviewTask.getCreator() != null ? requestReviewTask.getCreator().getEmail() : null;
        String creatorFullName = requestReviewTask.getCreator() != null ? requestReviewTask.getCreator().getFullName() : null;
        String creatorUserId = requestReviewTask.getCreator() != null ? requestReviewTask.getCreator().getUserId() : null;

        jdbcTemplate.update(stmt,
                requestReviewTask.getDueDate(), assigneeAbbreviation, assigneeDepartmentName,
                assigneeEmail, assigneeFullName, assigneeUserId,
                requestReviewTask.getCreatedOn(),
                creatorAbbreviation, creatorDepartmentName, creatorEmail, creatorFullName, creatorUserId,
                requestReviewTask.getStatus(), requestReviewTask.getReview().getId()
        );

        Long reviewTaskId = null;
        if (assigneeUserId != null)
            reviewTaskId = findReviewTaskIdByAssignee(assigneeUserId);
        else
            reviewTaskId = findReviewTaskIdByDueDate(requestReviewTask.getDueDate());

        Long nextVal = getNextHibernateSequence();
        stmt = "insert into aud_updater (abbreviation, department_name, email, full_name, " +
                "timestamp, user_id, id) values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt,
                creatorAbbreviation, creatorDepartmentName, creatorEmail, creatorFullName,
                (new Date()).getTime(), creatorUserId, nextVal);
        stmt = "insert into aud_review_task (revtype, revend, revend_tstmp, abbreviation, department_name, email, full_name,\n" +
                " user_id, assignee_mod, created_on, created_on_mod, creator_abbreviation, creator_department_name, creator_email,\n" +
                " creator_full_name, creator_user_id, creator_mod, due_date, due_date_mod, status, status_mod, review_id, review_mod, id, rev)\n" +
                " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, 1, null, null,
                creatorAbbreviation, creatorDepartmentName, creatorEmail,
                assigneeFullName, creatorUserId, false, requestReviewTask.getCreatedOn(),
                false, creatorAbbreviation, creatorDepartmentName,
                creatorEmail, creatorFullName,
                creatorUserId, false,
                requestReviewTask.getDueDate(), false, requestReviewTask.getStatus(), false, requestReviewTask.getReview().getId(), false, reviewTaskId, nextVal);

        return reviewTaskId;
    }

    public void setReviewTaskStatus(Long reviewTaskId, Integer status) throws Exception {
        String stmt = "update review_task set status = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, status, reviewTaskId);
        assert updatedRecords == 1;
    }


    public Long createReviewTaskAndSetStatus(String dataIdentifier, String properties, ReviewTaskStatus status, Long reviewId) throws Exception {
        Long reviewTaskId = createReviewTask(dataIdentifier, properties, reviewId);
        setReviewTaskStatus(reviewTaskId, status.getStatusCode());
        return reviewTaskId;
    }

    public Long createReviewEntry(String dataIdentifier, String properties, Long reviewId, Long reviewTaskId) {
        ReviewEntry reviewEntry = null;
        reviewEntry = EntityPojoFactory.createReviewEntry(dataIdentifier, properties);
        reviewEntry.setReview(new Review());
        reviewEntry.getReview().setId(reviewId);
        reviewEntry.setReviewTask(new ReviewTask());
        reviewEntry.getReviewTask().setId(reviewTaskId);

        String assigneeAbbreviation = reviewEntry.getAssignee() != null ? reviewEntry.getAssignee().getAbbreviation() : null;
        String assigneeDepartmentName = reviewEntry.getAssignee() != null ? reviewEntry.getAssignee().getDepartmentName() : null;
        String assigneeEmail = reviewEntry.getAssignee() != null ? reviewEntry.getAssignee().getEmail() : null;
        String assigneeFullName = reviewEntry.getAssignee() != null ? reviewEntry.getAssignee().getFullName() : null;
        String assigneeUserId = reviewEntry.getAssignee() != null ? reviewEntry.getAssignee().getUserId() : null;

        String creatorAbbreviation = reviewEntry.getCreator() != null ? reviewEntry.getCreator().getAbbreviation() : null;
        String creatorDepartmentName = reviewEntry.getCreator() != null ? reviewEntry.getCreator().getDepartmentName() : null;
        String creatorEmail = reviewEntry.getCreator() != null ? reviewEntry.getCreator().getEmail() : null;
        String creatorFullName = reviewEntry.getCreator() != null ? reviewEntry.getCreator().getFullName() : null;
        String creatorUserId = reviewEntry.getCreator() != null ? reviewEntry.getCreator().getUserId() : null;


        String stmt = "INSERT INTO public.review_entry(" +
                "assignee_abbreviation, assignee_department_name, assignee_email, assignee_full_name, " +
                "assignee_user_id, classification, created_on, creator_abbreviation, " +
                "creator_department_name, creator_email, creator_full_name, creator_user_id, " +
                "description,status,review_id,reviewtask_id) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        jdbcTemplate.update(stmt,
                assigneeAbbreviation, assigneeDepartmentName,
                assigneeEmail, assigneeFullName, assigneeUserId,
                reviewEntry.getClassification(), reviewEntry.getCreatedOn(),
                creatorAbbreviation, creatorDepartmentName, creatorEmail, creatorFullName, creatorUserId,
                reviewEntry.getDescription(),
                reviewEntry.getStatus(), reviewEntry.getReview().getId(), reviewEntry.getReviewTask().getId()
        );
        Long reviewEntryId = findReviewEntryIdByReviewTaskId(reviewId, reviewTaskId);

        int order = 0;
        for (ReviewEntryContext reviewEntryContext : reviewEntry.getContexts()) {
            stmt = "INSERT INTO review_entry_contexts(" +
                    "id, context_id, name, type, contexts_order) " +
                    "VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(stmt,
                    reviewEntryId, reviewEntryContext.getContextId(), reviewEntryContext.getName(), reviewEntryContext.getType(), order++);
        }

        Long nextVal = getNextHibernateSequence();
        stmt = "insert into aud_updater (abbreviation, department_name, email, full_name, " +
                "timestamp, user_id, id) values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt,
                creatorAbbreviation, creatorDepartmentName, creatorEmail, creatorFullName,
                (new Date()).getTime(), creatorUserId, nextVal);

        stmt = "INSERT INTO aud_review_entry(" +
                "id, rev, revtype, revend, revend_tstmp, assignee_abbreviation, assignee_department_name, assignee_email, assignee_full_name, assignee_user_id," +
                " assignee_mod, classification, classification_mod, created_on, created_on_mod, creator_abbreviation, creator_department_name, creator_email, creator_full_name, creator_user_id," +
                " creator_mod, description, description_mod, remark, remark_mod, sequence_number, sequence_number_mod, status, status_mod, contexts_mod, review_id, review_mod, reviewtask_id, review_task_mod)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, reviewEntryId, nextVal, 1, null, null, assigneeAbbreviation, assigneeDepartmentName, assigneeEmail, assigneeFullName, assigneeUserId,
                false, reviewEntry.getClassification(), false, reviewEntry.getCreatedOn(), false, creatorAbbreviation, creatorDepartmentName, creatorEmail, creatorFullName, creatorUserId,
                false, reviewEntry.getDescription(), false, reviewEntry.getRemark(), false, reviewEntry.getSequenceNumber(), false, reviewEntry.getStatus(), false, false, reviewId, false, reviewTaskId, false);
        return reviewEntryId;
    }

    public Long createReviewEntryAndSetStatus(String dataIdentifier, String properties, Long reviewId, Long reviewTaskId, ReviewEntryStatus status) {
        Long reviewEntryId = this.createReviewEntry(dataIdentifier, properties, reviewId, reviewTaskId);
        assert reviewEntryId != null;
        String stmt = "update review_entry set status = ?, sequence_number= ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, status.getStatusCode(), 1, reviewEntryId);
        assert updatedRecords == 1;
        return reviewEntryId;
    }
}

