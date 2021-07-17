CREATE INDEX IF NOT EXISTS
release_package_id_pkey ON
release_package (id);

CREATE INDEX IF NOT EXISTS
release_package_contexts_id_fkey ON
release_package_contexts (id)

CREATE INDEX IF NOT EXISTS
prerequisite_release_packages_id_fkey ON
prerequisite_release_packages (id)

CREATE INDEX IF NOT EXISTS
tags_id_fkey ON
tags (id)

CREATE INDEX IF NOT EXISTS
types_id_fkey ON
types (id)

CREATE INDEX IF NOT EXISTS
my_team_member_myteam_id_fkey ON
my_team_member (myteam_id)

CREATE INDEX IF NOT EXISTS
my_team_id_pkey ON
my_team (id);

CREATE INDEX IF NOT EXISTS
member_role_id_fkey ON
member_role (id)

CREATE INDEX IF NOT EXISTS
my_team_member_id_pkey ON
my_team_member (id);

CREATE INDEX IF NOT EXISTS
document_release_package_comment_id_fkey ON
document (release_package_comment_id);

CREATE INDEX IF NOT EXISTS
comment_id_pkey ON comment (id);

CREATE INDEX IF NOT EXISTS
release_package_status_col ON
release_package (status);

CREATE INDEX IF NOT EXISTS
release_package_title_col ON
release_package (title);

CREATE INDEX IF NOT EXISTS
release_package_project_id_col ON
release_package (project_id);

CREATE INDEX IF NOT EXISTS
release_package_product_id_col ON
release_package (product_id);

CREATE INDEX IF NOT EXISTS
aud_release_package_rev_fkey ON
aud_release_package (rev);

CREATE INDEX IF NOT EXISTS
aud_release_package_revend_fkey ON
aud_release_package (revend);


CREATE INDEX IF NOT EXISTS
aud_release_package_contexts_rev_fkey ON
aud_release_package_contexts (rev);

CREATE INDEX IF NOT EXISTS
aud_release_package_contexts_revend_fkey ON
aud_release_package_contexts (revend);


CREATE INDEX IF NOT EXISTS
aud_change_control_boards_rev_fkey ON
aud_change_control_boards (rev);

CREATE INDEX IF NOT EXISTS
aud_change_control_boards_revend_fkey ON
aud_change_control_boards (revend);

