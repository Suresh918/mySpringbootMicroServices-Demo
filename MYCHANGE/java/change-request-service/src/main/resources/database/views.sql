CREATE EXTENSION IF NOT EXISTS tablefunc;

DROP TABLE IF EXISTS summary;
CREATE OR REPLACE VIEW summary
AS (SELECT x.id,
           x.title,
           x.status,
           x.implementation_priority,
           z.customer_impact,z.preinstall_impact,y.change_notice_id
   FROM (SELECT change_request.id,change_request.title,change_request.status,change_request.implementation_priority
         FROM change_request) x
            LEFT JOIN (SELECT context_id as change_notice_id, id as change_request_id FROM change_request_contexts where type='CHANGENOTICE') as y ON x.id = y.change_request_id
            LEFT JOIN (SELECT impact_analysis.change_request_id, customer_impact.customer_impact_result as customer_impact,
                              preinstall_impact.preinstall_impact_result as preinstall_impact FROM impact_analysis
                                                                                     LEFT JOIN customer_impact ON impact_analysis.id=customer_impact.impact_analysis_id
                                                                                     LEFT JOIN preinstall_impact ON impact_analysis.id=preinstall_impact.impact_analysis_id) z
                      ON x.id = z.change_request_id);


DROP TABLE IF EXISTS member_overview;
CREATE OR REPLACE VIEW member_overview AS (SELECT myteam.id,
                                                                 myteam.change_request_id,
                                                                 myteam.member_count,
                                                                 String_agg(Concat(member_info.user_id, '~',  member_info.ROLES), ',') AS member_data
                                                         FROM (SELECT my_team.id, my_team.change_request_id,
                                                                      (SELECT Count(member.id) FROM my_team_member member WHERE  myteam_id = my_team.id)
                                                                          AS member_count FROM   my_team)myteam
                                                                  LEFT JOIN (SELECT member.id, member.myteam_id, member.user_id, role.ROLES
                                                                             FROM my_team_member member INNER JOIN member_role role ON role.id = member.id)member_info
                                                                            ON member_info.myteam_id = myteam.id
                                                         GROUP  BY myteam.id,
                                                                   myteam.change_request_id,
                                                                   myteam.member_count);

DROP TABLE IF EXISTS overview_actions;
CREATE OR REPLACE VIEW overview_actions AS
(SELECT cr.id as change_request_id, cr.status as change_request_status,y.member_data,
        sum(case when crc.status IN ('OPEN','ACCEPTED') then 1 else 0 end) AS open_actions,
        sum(case when crc.status IN ('COMPLETED','REJECTED') then 1 else 0 end) AS completed_actions
        from change_request cr JOIN change_request_contexts crc ON cr.id=crc.id and crc.type='ACTION'
         LEFT JOIN member_overview y
                      ON cr.id = y.change_request_id GROUP BY cr.id,y.member_data);

DROP VIEW IF EXISTS change_boards_summary;
CREATE OR REPLACE VIEW change_boards_summary AS
(SELECT id as change_request_id, Concat(',', String_agg(change_boards.change_boards :: text, ',' :: text) , ',') AS change_boards
           FROM   change_boards GROUP BY id);

DROP VIEW IF EXISTS change_control_boards_summary;
CREATE OR REPLACE VIEW change_control_boards_summary AS
(SELECT id as change_request_id, Concat(',', String_agg(change_control_boards.change_control_boards :: text, ',' :: text), ',') AS change_control_boards
           FROM   change_control_boards GROUP BY id);

DROP TABLE IF EXISTS overview;
CREATE OR replace VIEW overview
AS (SELECT x.id,
           x.title,
           x.status,
           x.analysis_priority,
           x.change_owner_type,
           y.member_count,
           y.member_data,
           z.impact,
           coalesce(w.open_actions, 0) as open_actions,
           coalesce(w.completed_actions,0) as completed_actions,
           coalesce(w.total_actions,0) as total_actions
   FROM (SELECT change_request.id,change_request.title,change_request.status,change_request.analysis_priority,change_request.change_owner_type
         FROM change_request) x
            LEFT JOIN member_overview y
                      ON x.id = y.change_request_id
            LEFT JOIN (SELECT impact_analysis.change_request_id, customer_impact.customer_impact_result as impact FROM impact_analysis LEFT JOIN customer_impact ON impact_analysis.id=customer_impact.impact_analysis_id) z
                      ON x.id = z.change_request_id
            LEFT JOIN (SELECT change_request_id, open_actions, completed_actions, sum(open_actions + completed_actions) as total_actions
                       FROM overview_actions GROUP BY change_request_id, open_actions,completed_actions) w
                      ON x.id= w.change_request_id);

DROP TABLE IF EXISTS search_summary;
CREATE OR REPLACE VIEW search_summary AS
(SELECT x.id,
       x.title,
       x.status,
       x.change_specialist1_user_id as change_specialist1user_id,
       x.change_specialist1_email as change_specialist1email,
       x.change_specialist1_full_name as change_specialist1full_name,
       x.change_specialist1_department_name as change_specialist1department_name,
       x.change_specialist1_abbreviation as change_specialist1abbreviation,
       x.analysis_priority,
       cb.change_boards,
       ccb.change_control_boards,
       COALESCE(w.open_actions, 0)              AS open_actions,
       COALESCE(w.completed_actions, 0)         AS completed_actions,
       COALESCE(w.total_actions, 0)             AS total_actions
FROM   (SELECT change_request.id,
               change_request.title,
               change_request.status,
               change_request.analysis_priority,
               change_request.change_specialist1_user_id,
               change_request.change_specialist1_email,
               change_request.change_specialist1_full_name,
               change_request.change_specialist1_department_name,
               change_request.change_specialist1_abbreviation
        FROM   change_request) x
       LEFT JOIN (SELECT change_request_id,
                         open_actions,
                         completed_actions,
                         Sum(open_actions + completed_actions) AS total_actions
                  FROM   overview_actions
                  GROUP  BY change_request_id,
                            open_actions,
                            completed_actions) w
              ON x.id = w.change_request_id
       LEFT JOIN change_boards_summary cb
                      ON x.id = cb.change_request_id
       LEFT JOIN change_control_boards_summary ccb
                      ON x.id = ccb.change_request_id)

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
    comment.change_request_id AS parent_id,
    comment.replyto_id AS parent_comment_id
   FROM comment)

DROP VIEW IF EXISTS document_tags;
CREATE OR REPLACE VIEW document_tags AS
(SELECT id as document_id, Concat(',', String_agg(tags, ','), ',') AS tags
        FROM   document_tag GROUP BY id);

DROP TABLE IF EXISTS document_overview;
CREATE OR REPLACE VIEW document_overview AS
(SELECT document.id,
       document.created_on,
       document.name,
       document.creator_user_id,
       document.creator_full_name,
       document.creator_department_name,
       document.creator_email,
 	   document.description,
 	   document.creator_abbreviation,
 	   document.type,
 		document.size,
       COALESCE(document.change_request_id, comment.change_request_id) AS
       parent_id,
       document.change_request_comment_id as parent_comment_id,
       document_tags.tags
FROM   document
       LEFT JOIN comment ON comment.id = document.change_request_comment_id
       LEFT JOIN document_tags ON document_tags.document_id = document.id)

DROP TABLE IF EXISTS agenda_link_overview;
CREATE OR replace VIEW agenda_link_overview AS
  (SELECT x.id,
          x.title,
          x.status,
          x.implementation_priority,
          x.analysis_priority,
          x.requirements_for_implementation_plan,
          x.project_id,
          x.change_specialist2_user_id               AS change_specialist2user_id,
          x.change_specialist2_email                 AS change_specialist2email,
          x.change_specialist2_full_name             AS change_specialist2full_name,
          x.change_specialist2_department_name       AS change_specialist2department_name,
          x.change_specialist2_abbreviation          AS change_specialist2abbreviation,
          cb.change_boards,
          ccb.change_control_boards,
          Coalesce(w.open_actions, 0 :: bigint)      AS open_actions,
          Coalesce(w.completed_actions, 0 :: bigint) AS completed_actions,
          Coalesce(w.total_actions, 0 :: NUMERIC)    AS total_actions
   FROM   (SELECT change_request.id,
                  change_request.change_specialist2_user_id,
                  change_request.change_specialist2_email,
                  change_request.change_specialist2_full_name,
                  change_request.change_specialist2_department_name,
                  change_request.change_specialist2_abbreviation,
                  change_request.project_id,
                  change_request.title,
                  change_request.status,
                  change_request.implementation_priority,
                  change_request.analysis_priority,
                  change_request.requirements_for_implementation_plan
           FROM   change_request) x
          LEFT JOIN (SELECT overview_actions.change_request_id,
                            overview_actions.open_actions,
                            overview_actions.completed_actions,
                            sum(overview_actions.open_actions + overview_actions.completed_actions) AS total_actions
                     FROM   overview_actions
                     GROUP  BY
overview_actions.change_request_id,
overview_actions.open_actions,
overview_actions.completed_actions)w ON x.id = w.change_request_id
          LEFT JOIN change_boards_summary cb
              ON cb.change_request_id = x.id
          LEFT JOIN change_control_boards_summary ccb
              ON ccb.change_request_id = x.id);

DROP TABLE IF EXISTS trackerboard_summary;
CREATE OR replace VIEW trackerboard_summary AS
(SELECT c.id,
       c.project_id,
       c.title,
       c.status
FROM   change_request c);
