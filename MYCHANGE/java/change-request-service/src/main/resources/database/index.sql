CREATE INDEX IF NOT EXISTS
change_request_id_pkey ON
change_request (id);

CREATE INDEX IF NOT EXISTS
change_request_contexts_id_fkey ON
change_request_contexts (id)

CREATE INDEX IF NOT EXISTS
impact_analysis_change_request_id_fkey ON
impact_analysis (change_request_id)

CREATE INDEX IF NOT EXISTS
solution_definition_change_request_id_fkey ON
solution_definition (change_request_id)

CREATE INDEX IF NOT EXISTS
scope_change_request_id_fkey ON
scope (change_request_id)

CREATE INDEX IF NOT EXISTS
impact_analysis_id_pkey ON
impact_analysis (id)

CREATE INDEX IF NOT EXISTS
customer_impact_impact_analysis_id_fkey ON
customer_impact (impact_analysis_id)

CREATE INDEX IF NOT EXISTS
preinstall_impact_impact_analysis_id_fkey ON
preinstall_impact (impact_analysis_id)

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
document_change_request_comment_id_fkey ON
document (change_request_comment_id);

CREATE INDEX IF NOT EXISTS
comment_id_pkey ON comment (id);

CREATE INDEX IF NOT EXISTS
change_request_status_col ON
change_request (status);

CREATE INDEX IF NOT EXISTS
change_request_title_col ON
change_request (title);

CREATE INDEX IF NOT EXISTS
change_request_project_id_col ON
change_request (project_id);

CREATE INDEX IF NOT EXISTS
change_request_product_id_col ON
change_request (product_id);

CREATE INDEX IF NOT EXISTS
change_request_analysis_priority_col ON
change_request (analysis_priority);

CREATE INDEX IF NOT EXISTS
change_request_implementation_priority_col ON
change_request (implementation_priority);

CREATE INDEX IF NOT EXISTS
aud_change_request_rev_fkey ON
aud_change_request(rev);

CREATE INDEX IF NOT EXISTS
aud_change_request_revend_fkey ON
aud_change_request (revend);

CREATE INDEX IF NOT EXISTS
aud_solution_definition_rev_fkey ON
aud_solution_definition (rev);

CREATE INDEX IF NOT EXISTS
aud_solution_definition_revend_fkey ON
aud_solution_definition (revend);

CREATE INDEX IF NOT EXISTS
aud_scope_rev_fkey ON
aud_scope (rev);

CREATE INDEX IF NOT EXISTS
aud_scope_revend_fkey ON
aud_scope (revend);

CREATE INDEX IF NOT EXISTS
aud_impact_analysis_rev_fkey ON
aud_impact_analysis (rev);

CREATE INDEX IF NOT EXISTS
aud_impact_analysis_revend_fkey ON
aud_impact_analysis (revend);

CREATE INDEX IF NOT EXISTS
aud_customer_impact_rev_fkey ON
aud_customer_impact (rev);

CREATE INDEX IF NOT EXISTS
aud_customer_impact_revend_fkey ON
aud_customer_impact (revend);

CREATE INDEX IF NOT EXISTS
aud_preinstall_impact_rev_fkey ON
aud_preinstall_impact (rev);

CREATE INDEX IF NOT EXISTS
aud_preinstall_impact_revend_fkey ON
aud_preinstall_impact (revend);

CREATE INDEX IF NOT EXISTS
aud_complete_business_case_impact_rev_fkey ON
aud_complete_business_case (rev);

CREATE INDEX IF NOT EXISTS
aud_complete_business_case_revend_fkey ON
aud_complete_business_case (revend);

CREATE INDEX IF NOT EXISTS
aud_change_request_contexts_rev_fkey ON
aud_change_request_contexts (rev);

CREATE INDEX IF NOT EXISTS
aud_change_request_contexts_revend_fkey ON
aud_change_request_contexts (revend);

CREATE INDEX IF NOT EXISTS
aud_cbp_strategies_rev_fkey ON
aud_cbp_strategies (rev);

CREATE INDEX IF NOT EXISTS
aud_cbp_strategies_revend_fkey ON
aud_cbp_strategies (revend);

CREATE INDEX IF NOT EXISTS
aud_change_boards_rev_fkey ON
aud_change_boards (rev);

CREATE INDEX IF NOT EXISTS
aud_change_boards_revend_fkey ON
aud_change_boards (revend);

CREATE INDEX IF NOT EXISTS
aud_change_control_boards_rev_fkey ON
aud_change_control_boards (rev);

CREATE INDEX IF NOT EXISTS
aud_change_control_boards_revend_fkey ON
aud_change_control_boards (revend);

CREATE INDEX IF NOT EXISTS
aud_dependent_change_requests_rev_fkey ON
aud_dependent_change_requests (rev);

CREATE INDEX IF NOT EXISTS
aud_dependent_change_requests_revend_fkey ON
aud_dependent_change_requests (revend);

CREATE INDEX IF NOT EXISTS
aud_fco_types_rev_fkey ON
aud_fco_types (rev);

CREATE INDEX IF NOT EXISTS
aud_fco_types_revend_fkey ON
aud_fco_types (revend);

CREATE INDEX IF NOT EXISTS
aud_implementation_ranges_rev_fkey ON
aud_implementation_ranges (rev);

CREATE INDEX IF NOT EXISTS
aud_implementation_ranges_revend_fkey ON
aud_implementation_ranges (revend);

CREATE INDEX IF NOT EXISTS
aud_issue_types_rev_fkey ON
aud_issue_types (rev);

CREATE INDEX IF NOT EXISTS
aud_issue_types_revend_fkey ON
aud_issue_types (revend);

CREATE INDEX IF NOT EXISTS
aud_products_affected_rev_fkey ON
aud_products_affected (rev);

CREATE INDEX IF NOT EXISTS
aud_products_affected_revend_fkey ON
aud_products_affected (revend);
