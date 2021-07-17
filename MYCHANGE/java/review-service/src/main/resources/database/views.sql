CREATE EXTENSION IF NOT EXISTS tablefunc;

DROP TABLE IF EXISTS review_context_pivot;

CREATE OR REPLACE VIEW review_context_pivot AS
(SELECT x.review_id,
    x.releasepackage AS releasepackage_id,
    y.releasepackage_name,
    x.ecn AS ecn_id,
    y.ecn_name,
 	x.teamcenter AS teamcenter_id,
  	y.teamcenter_name
   FROM (SELECT final_result.review_id,
            final_result.ecn,
            final_result.releasepackage,
		 	final_result.teamcenter
           FROM crosstab('select id as review_id , type, context_id from review_contexts order by 1, 2'::text) final_result(review_id bigint, ecn character varying, releasepackage character varying, teamcenter character varying)) x
     JOIN ( SELECT o1.review_id,
            o1.releasepackage_name,
            o2.ecn_name,
		   o3.teamcenter_name
           FROM ( SELECT review_contexts.id AS review_id,
                    review_contexts.name AS releasepackage_name
                   FROM review_contexts
                  WHERE review_contexts.type::text = 'RELEASEPACKAGE'::text) o1
             LEFT JOIN LATERAL ( SELECT review_contexts.name AS ecn_name
                   FROM review_contexts
                  WHERE review_contexts.type::text = 'ECN'::text AND review_contexts.id = o1.review_id) o2 ON true
		   LEFT JOIN LATERAL ( SELECT review_contexts.name AS teamcenter_name
                   FROM review_contexts
                  WHERE review_contexts.type::text = 'TEAMCENTER'::text AND review_contexts.id = o1.review_id) o3
             ON true) y ON x.review_id = y.review_id);

DROP TABLE IF EXISTS review_summary;
CREATE OR REPLACE VIEW review_summary AS
(SELECT review.id,
          review.title,
          count(review_task.review_id) AS review_task_count ,
          review.completion_date,
          review.status,
     (SELECT count(review_id)
      FROM review_task
      WHERE status in (4,5,6)
        AND review_id=review.id) AS completed_review_task_count,
         review_context_pivot.releasepackage_id,
        review_context_pivot.releasepackage_name,
         review_context_pivot.ecn_id,
        review_context_pivot.ecn_name
   FROM review
   LEFT JOIN review_task ON review.id=review_task.review_id
   LEFT JOIN review_context_pivot ON review.id=review_context_pivot.review_id
   GROUP BY review.id,
            review.title,
         review_context_pivot.releasepackage_id,
         review_context_pivot.releasepackage_name,
         review_context_pivot.ecn_id,
         review_context_pivot.ecn_name);

DROP TABLE IF EXISTS review_overview;
CREATE OR REPLACE VIEW review_overview AS
    (SELECT review.id,
          review.title,
          review.completion_date,
          review.status,
   (SELECT concat(',',string_agg(concat(user_id,':',status), ','), ',') AS string_agg FROM review_task WHERE review_id=review.id) AS review_tasks_assignee_info,
   (SELECT concat(',',string_agg(assignee_user_id, ','), ',') AS string_agg FROM review_entry WHERE review_entry.review_id = review.id) AS review_entry_assignees,
   (SELECT count(review_id) FROM review_task WHERE review_id=review.id) AS review_task_count,
   (SELECT count(review_id) FROM review_entry WHERE review_id=review.id) AS review_entry_count,
   (SELECT count(review_id) FROM review_task WHERE status in (4,5,6) AND review_id=review.id) AS completed_review_task_count,
   (SELECT count(review_entry.review_id) FROM review_entry WHERE review_entry.status in (3,4,5)
            AND review_entry.review_id = review.id) AS completed_review_entry_count,
            review_context_pivot.releasepackage_id,
            review_context_pivot.releasepackage_name,
            review_context_pivot.ecn_id,
            review_context_pivot.ecn_name,
            review_context_pivot.teamcenter_id,
            review_context_pivot.teamcenter_name
   FROM review LEFT JOIN review_context_pivot ON review.id=review_context_pivot.review_id
   GROUP BY review.id,
            review.title,
            review.completion_date,
            review_context_pivot.releasepackage_id,
            review_context_pivot.releasepackage_name,
            review_context_pivot.ecn_id,
            review_context_pivot.ecn_name,
            review_context_pivot.teamcenter_id,
            review_context_pivot.teamcenter_name);

DROP TABLE IF EXISTS review_task_summary;
CREATE OR REPLACE VIEW review_task_summary AS
(SELECT review_task.id,
          review_task.user_id,
          review_task.full_name,
          review_task.abbreviation,
          review_task.department_name,
          review_task.email,
          review_task.status,
          review_task.due_date,
          count(review_entry.reviewtask_id) review_entry_count,
     (SELECT count(review_entry.reviewtask_id)
      FROM review_entry
      WHERE review_entry.status in (3,4,5)
AND review_entry.reviewtask_id = review_task.id) AS completed_review_entry_count
   FROM review_task
   LEFT JOIN review_entry ON review_task.id=review_entry.reviewtask_id
   GROUP BY review_task.id,
            review_task.user_id,
            review_task.full_name,
            review_task.abbreviation,
            review_task.status,
            review_task.due_date);

DROP TABLE IF EXISTS review_entry_overview;
CREATE OR REPLACE VIEW review_entry_overview AS
((SELECT review_entry.id,
          review_entry.description,
          review_entry.remark,
          review_entry.sequence_number,
          review_entry.assignee_user_id,
          review_entry.assignee_full_name,
          review_entry.assignee_abbreviation,
          review_entry.assignee_department_name,
          review_entry.assignee_email,
          review_entry.creator_user_id,
          review_entry.creator_full_name,
          review_entry.creator_abbreviation,
          review_entry.creator_department_name,
          review_entry.creator_email,
          review_entry.status,
          review_entry.classification,
      (SELECT count(id) from comment where reviewentry_id =review_entry.id and status = 2) as comment_count,
      (select string_agg(name,',') from review_entry_contexts where id=review_entry.id) as solution_items
      FROM review_entry)
);

DROP TABLE IF EXISTS comment_overview;
 CREATE OR REPLACE VIEW comment_overview AS
(SELECT comment.id,
    comment.comment_text,
    comment.status,
    comment.creator_user_id,
    comment.creator_full_name,
    comment.creator_abbreviation,
    comment.creator_department_name,
    comment.creator_email,
    comment.created_on,
    ( SELECT count(c.id) AS count
           FROM comment c
          WHERE c.replyto_id = comment.id AND c.status = 2) AS reply_count,
    comment.reviewentry_id AS parent_id,
    comment.replyto_id AS parent_comment_id
   FROM comment)
