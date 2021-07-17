DROP VIEW IF EXISTS comment_overview;
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
    comment.release_package_id AS parent_id,
    comment.replyto_id AS parent_comment_id
   FROM comment);

DROP VIEW IF EXISTS document_tags cascade;
CREATE OR REPLACE VIEW document_tags AS
(SELECT id as document_id, Concat(',', String_agg(tags, ','), ',') AS tags
        FROM   document_tag GROUP BY id);

DROP TABLE IF EXISTS document_overview cascade;
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
       COALESCE(document.release_package_id, comment.release_package_id) AS
       parent_id,
       document.release_package_comment_id as parent_comment_id,
       document_tags.tags
FROM   document
       LEFT JOIN comment ON comment.id = document.release_package_comment_id
       LEFT JOIN document_tags ON document_tags.document_id = document.id);

DROP VIEW IF EXISTS date_action_count;
CREATE OR REPLACE VIEW date_action_count AS (
SELECT rp.id,
       rp.status,
       CASE WHEN (planned_effective_date IS NOT NULL AND planned_effective_date < current_date) THEN 1 ELSE 0 END planned_effective_date_past,
       CASE WHEN (planned_release_date IS NOT NULL AND planned_release_date < current_date) THEN 1 ELSE 0 END planned_release_date_past,
       CASE WHEN (planned_effective_date IS NOT NULL AND planned_effective_date >= current_date AND planned_effective_date < current_date +8 ) THEN 1 ELSE 0 END planned_effective_date_soon,
       CASE WHEN (planned_release_date IS NOT NULL AND planned_release_date >= current_date AND planned_release_date < current_date +8 ) THEN 1 ELSE 0 END planned_release_date_soon,
       SUM (CASE WHEN rpc.id is not null THEN 1 ELSE 0 END) action_count
FROM   release_package rp
           LEFT OUTER JOIN release_package_contexts rpc ON rp.id = rpc.id and rpc.type='ACTION' AND rpc.status IN ('OPEN', 'ACCEPTED')
GROUP BY rp.id);

DROP VIEW IF EXISTS actions_overview CASCADE ;
CREATE OR REPLACE VIEW actions_overview AS
(SELECT id as release_package_id,
        sum(case when status IN ('OPEN','ACCEPTED') then 1 else 0 end) AS open_actions,
        sum(case when status IN ('COMPLETED','REJECTED') then 1 else 0 end) AS completed_actions
        from release_package_contexts where type='ACTION' GROUP BY release_package_id);

DROP VIEW IF EXISTS member_overview;
CREATE OR REPLACE VIEW member_overview AS (SELECT myteam.id,
 myteam.release_package_id, myteam.member_count,String_agg(Concat(member_info.user_id, '~',  member_info.ROLES), ',')
  AS member_data FROM (SELECT my_team.id, my_team.release_package_id,
  (SELECT Count(member.id) FROM my_team_member member WHERE  myteam_id = my_team.id)
   AS member_count FROM   my_team)myteam
   LEFT JOIN (SELECT member.id, member.myteam_id, member.user_id, role.ROLES
FROM my_team_member member INNER JOIN member_role role ON role.id = member.id)member_info
 ON member_info.myteam_id = myteam.id GROUP  BY myteam.id, myteam.release_package_id,
 myteam.member_count);

DROP VIEW IF EXISTS ecn_contexts_overview CASCADE;
create or replace view ecn_contexts_overview
as (select x.id,y.context_id from release_package x,release_package_contexts y
  where x.id=y.id and y.type='ECN');

DROP VIEW IF EXISTS mdgcr_contexts_overview CASCADE;
create or replace view mdgcr_contexts_overview
as (select x.id,y.context_id from release_package x,release_package_contexts y
  where x.id=y.id and y.type='MDG-CR');

DROP VIEW IF EXISTS teamcenter_contexts_overview CASCADE;
create or replace view teamcenter_contexts_overview
as (select x.id,y.context_id from release_package x,release_package_contexts y
  where x.id=y.id and y.type='TEAMCENTER');

DROP TABLE IF EXISTS overview CASCADE;
CREATE OR replace VIEW overview
AS (SELECT x.id,
	x.release_package_number,
           x.title,
           x.status,
           x.planned_effective_date,
           x.planned_release_date,
           x.project_id,
		   x.sap_change_control,
		   x.er_valid_from_input_strategy,
		   x.change_owner_type,
           e.context_id as ecn_number,
           m.context_id as mdg_cr_id,
           t.context_id as team_center_id,

	rpm.member_count,
	rpm.member_data,
		coalesce(w.open_actions, 0) as open_actions,
           coalesce(w.completed_actions,0) as completed_actions,
           coalesce(w.total_actions,0) as total_actions
   FROM (SELECT release_package.id,release_package.release_package_number,
		 release_package.title,release_package.status,release_package.planned_effective_date,
		 release_package.planned_release_date,release_package.project_id,release_package.sap_change_control,release_package.er_valid_from_input_strategy,
		 release_package.change_owner_type
     FROM release_package) x
            left JOIN ecn_contexts_overview e
	on x.id=e.id
	left JOIN mdgcr_contexts_overview m
	on x.id=m.id
	left JOIN teamcenter_contexts_overview t
	on x.id=t.id
	LEFT JOIN member_overview rpm
                      ON x.id = rpm.release_package_id
			 LEFT JOIN (SELECT release_package_id, open_actions, completed_actions, sum(open_actions + completed_actions)
						as total_actions
                       FROM actions_overview GROUP BY release_package_id, open_actions,completed_actions) w
                      ON x.id= w.release_package_id);

DROP TABLE IF EXISTS prerequisites_overview CASCADE;
CREATE OR replace VIEW prerequisites_overview
AS (SELECT y.id as id,x.id as prerequisite_release_package_id,
	x.release_package_number,
           x.title,
           x.status,
           x.planned_effective_date,
           x.planned_release_date,
		  y.sequence as sequence_number,
		w.member_count,
	w.member_data,
	w.open_actions,
    w.completed_actions,
    w.total_actions,
    w.ecn_number as ecn
   FROM (SELECT release_package.id,release_package.release_package_number,
		 release_package.title,release_package.status,release_package.planned_effective_date,
		 release_package.planned_release_date
     FROM release_package) x
            right JOIN prerequisite_release_packages y
	on x.id=y.release_package_id
			left join overview w
			on x.id=w.id and y.id=w.id);

DROP VIEW IF EXISTS prerequisites_summary;
CREATE OR REPLACE VIEW prerequisites_summary AS
    (SELECT id as release_package_id, Concat(',', String_agg(prerequisite_release_packages.release_package_id :: text, ',' :: text) , ',') AS prerequisite_release_packages
     FROM   prerequisite_release_packages GROUP BY id);


drop VIEW IF EXISTS search_summary;
CREATE OR replace VIEW search_summary
AS (select y.id,y.release_package_number,x.context_id as ecn,y.title,y.status,ps.prerequisite_release_packages as prerequisite_release_packageIds
    from ecn_contexts_overview x
   LEFT JOIN release_package y ON x.id=y.id
    LEFT JOIN prerequisites_summary ps
                      ON x.id = ps.release_package_id);

drop VIEW IF EXISTS automatic_closure;
CREATE or REPLACE VIEW automatic_closure (id,release_package_number,status,teamcenter_id,ecn_id,open_action_count,completed_review_count,total_review_count)AS
SELECT
    rp.id,
    rp.release_package_number,
    rp.status,
    (select context_id from release_package_contexts rpc where rpc.type='TEAMCENTER' and rpc.id=rp.id),
    (select context_id from release_package_contexts rpc where rpc.type='ECN' and rpc.id=rp.id),
    (select count(*) from release_package_contexts rpc where rpc.type='ACTION' and rpc.status in ('OPEN','ACCEPTED','DRAFT') and rpc.id=rp.id),
    (select count(*) from release_package_contexts rpc where rpc.type='REVIEW' and rpc.status='4' and rpc.id=rp.id),
    (select count(*) from release_package_contexts rpc where rpc.type='REVIEW' and rpc.id=rp.id)
FROM release_package rp
WHERE rp.status = 4;
