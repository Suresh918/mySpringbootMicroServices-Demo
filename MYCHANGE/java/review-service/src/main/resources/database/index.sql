CREATE INDEX IF NOT EXISTS
review_entry_review_id_fkey ON
review_entry (review_id);

CREATE INDEX IF NOT EXISTS
review_entry_reviewtask_id_fkey ON
review_entry (reviewtask_id);

CREATE INDEX IF NOT EXISTS
review_task_review_id_fkey ON
review_task (review_id);

CREATE INDEX IF NOT EXISTS
aud_review_entry_rev_fkey ON
aud_review_entry (rev);

CREATE INDEX IF NOT EXISTS
aud_review_entry_revend_fkey ON
aud_review_entry (revend);


CREATE INDEX IF NOT EXISTS
aud_review_task_rev_fkey ON
aud_review_task (rev);

CREATE INDEX IF NOT EXISTS
aud_review_task_revend_fkey ON
aud_review_task (revend);

CREATE INDEX IF NOT EXISTS
aud_review_entry_contexts_revend_fkey ON
aud_review_entry_contexts (revend);

CREATE INDEX IF NOT EXISTS
aud_review_contexts_revend_fkey ON
aud_review_contexts (revend);

CREATE INDEX IF NOT EXISTS
aud_review_rev_fkey ON
aud_review(rev);

CREATE INDEX IF NOT EXISTS
aud_review_revend_fkey ON
aud_review (revend);

CREATE INDEX IF NOT EXISTS
comment_reviewentry_id_fkey ON
comment(reviewentry_id);

CREATE INDEX IF NOT EXISTS
comment_replyto_id_fkey ON
comment(replyto_id);

CREATE INDEX IF NOT EXISTS
document_reviewentrycomment_id_fkey ON
document(reviewentrycomment_id);
