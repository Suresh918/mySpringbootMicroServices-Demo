package com.example.mirai.projectname.releasepackageservice.fixtures;


import com.example.mirai.libraries.comment.model.CommentStatus;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.document.model.DocumentStatus;
import com.example.mirai.projectname.releasepackageservice.comment.model.ReleasePackageComment;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageCommentDocument;
import com.example.mirai.projectname.releasepackageservice.document.model.ReleasePackageDocument;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EntityInstanceManager {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long findByReleasePackageId(String releasePackageId) {
        String sql = "SELECT ID FROM RELEASE_PACKAGE WHERE RELEASE_PACKAGE_NUMBER= ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{releasePackageId}, (rs, rowNum) ->
                rs.getLong("id"));

    }

    public String getReleasePackageIdById(Long id) {
        String sql = "SELECT RELEASE_PACKAGE_NUMBER FROM RELEASE_PACKAGE WHERE ID= ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                rs.getString("release_package_number"));
    }

    public Long getNextHibernateSequence() {
        String sql = "select nextval ('hibernate_sequence') as nextval";
        return jdbcTemplate.queryForObject(sql, new Object[]{}, (rs, rowNum) ->
                rs.getLong("nextval"));

    }

    public void updateReleasePackageAndSetIsSecure(Long releasePackageId, boolean isSecure) {
        String stmt = "update release_package set is_secure = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, isSecure, releasePackageId);
        assert updatedRecords == 1;
    }

    public Long updateReleasePackageContextAndSetStatus(Long id, String status, String type) {
        String stmt = "update release_package_contexts set status = ? where id = ? and type = ?";
        int updatedRecords = jdbcTemplate.update(stmt, status, id, type);
        assert updatedRecords == 1;
        return id;
    }

    public Long updateReleasePackageContextIdByType(Long id, String contextId, String type) {
        String stmt = "update release_package_contexts set context_id = ? where id = ? and type = ?";
        int updatedRecords = jdbcTemplate.update(stmt, contextId, id, type);
        assert updatedRecords == 1;
        return id;
    }
    public Long findMyTeamIdByReleasePackageId(Long releasePackageId) {
        String sql = "SELECT ID FROM MY_TEAM WHERE RELEASE_PACKAGE_ID=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{releasePackageId}, (rs, rowNum) ->
                rs.getLong("id"));
    }

    public Long findMyTeamMemberIdByTeamId(Long myTeamid) {
        String sql = "SELECT ID FROM MY_TEAM_MEMBER WHERE MYTEAM_ID=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{myTeamid}, (rs, rowNum) ->
                rs.getLong("id"));
    }

    public Long findMemberRoleByTeamId(Long myTeamid) {
        String sql = "SELECT ID FROM MEMBER_ROLE WHERE ID=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{myTeamid}, (rs, rowNum) ->
                rs.getLong("id"));
    }

    public String getMemberRolesById(Long myTeamid) {
        String sql = "SELECT ROLES FROM MEMBER_ROLE WHERE ID=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{myTeamid}, (rs, rowNum) ->
                rs.getString("roles"));
    }

    public Long createReleasePackageAndSetStatus(String dataIdentifier, String properties, ReleasePackageStatus status) {
        Long id = createReleasePackage(dataIdentifier, properties);
        Long myteamId = createMyTeam(id);
        assert myteamId != null;
        assert id != null;
        String stmt = "update release_package set status = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, status.getStatusCode(), id);
        assert updatedRecords == 1;

        return id;
    }

    public Long createReleasePackage(String dataIdentifier, String properties  ) {
        ReleasePackage releasePackage = null;
        releasePackage = EntityPojoFactory.createReleasePackage(dataIdentifier, properties);

        String stmt = "INSERT INTO public.release_package(" +
                "change_specialist_3_abbreviation, change_specialist_3_department_name, change_specialist_3_email, change_specialist_3_full_name, change_specialist_3_user_id, created_on, creator_abbreviation, creator_department_name, creator_email, creator_full_name, creator_user_id, executor_abbreviation, executor_department_name, executor_email, executor_full_name, executor_user_id, planned_effective_date, planned_release_date, prerequisites_applicable, prerequisites_detail, product_id, " +
                "project_id, release_package_number, sap_change_control, status, title, is_secure, plm_coordinator_user_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
        jdbcTemplate.update(stmt, releasePackage.getChangeSpecialist3().getAbbreviation(), releasePackage.getChangeSpecialist3().getDepartmentName(), releasePackage.getChangeSpecialist3().getEmail(), releasePackage.getChangeSpecialist3().getFullName(), releasePackage.getChangeSpecialist3().getUserId(),
                releasePackage.getCreatedOn(), releasePackage.getCreator().getAbbreviation(), releasePackage.getCreator().getDepartmentName(), releasePackage.getCreator().getEmail(), releasePackage.getCreator().getFullName(), releasePackage.getCreator().getUserId(),
                releasePackage.getExecutor().getAbbreviation(), releasePackage.getExecutor().getDepartmentName(), releasePackage.getExecutor().getEmail(), releasePackage.getExecutor().getFullName(), releasePackage.getExecutor().getUserId(), releasePackage.getPlannedEffectiveDate(),
                releasePackage.getPlannedReleaseDate(), null, null, null, null, releasePackage.getReleasePackageNumber(), releasePackage.getSapChangeControl(), releasePackage.getStatus(), releasePackage.getTitle(), false, releasePackage.getPlmCoordinator().getUserId());

        long releasePackageId = findByReleasePackageId(releasePackage.getReleasePackageNumber());

        long nextVal = getNextHibernateSequence();
        int order = 0;
        for (ReleasePackageContext releasePackageContext : releasePackage.getContexts()) {
            stmt = "INSERT INTO release_package_contexts(" +
                    "id, context_id, name, type, status, contexts_order) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(stmt,
                    releasePackageId, releasePackageContext.getContextId(), releasePackageContext.getName(), releasePackageContext.getType(), releasePackageContext.getStatus(), order++);
        }

        List<String> tags = EntityPojoFactory.addReleasePackageTags(dataIdentifier);
        int i=0;
        for(String tag : tags) {
                stmt = "INSERT INTO public.tags(id, tags, tags_order) VALUES (?, ?, ?)";
                jdbcTemplate.update(stmt, releasePackageId, tag, i++);
            }

        List<String> types = EntityPojoFactory.addReleasePackageTypes();
        int j=0;
        for(String type : types) {
            stmt = "INSERT INTO public.types(id, types, types_order) VALUES (?, ?, ?)";
            jdbcTemplate.update(stmt, releasePackageId, type, j++);
        }

        stmt = "insert into aud_updater (abbreviation, department_name, email, full_name, " +
                "timestamp, user_id, id) values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt,
                releasePackage.getCreator().getAbbreviation(), releasePackage.getCreator().getDepartmentName(), releasePackage.getCreator().getEmail(), releasePackage.getCreator().getFullName(),
                (new Date()).getTime(), releasePackage.getCreator().getUserId(), nextVal);

        stmt = "INSERT INTO public.aud_release_package" +
                "(id,rev, revtype, revend, revend_tstmp, change_specialist_3_abbreviation, change_specialist_3_department_name, change_specialist_3_email, change_specialist_3_full_name, change_specialist_3_user_id, " +
                "change_specialist3_mod, created_on, created_on_mod, creator_abbreviation, creator_department_name, creator_email, creator_full_name, creator_user_id, creator_mod, executor_abbreviation, " +
                "executor_department_name, executor_email, executor_full_name, executor_user_id, executor_mod, planned_effective_date, planned_effective_date_mod, planned_release_date, planned_release_date_mod, " +
                "prerequisites_applicable, prerequisites_applicable_mod, prerequisites_detail, prerequisites_detail_mod, product_id, product_id_mod, project_id, project_id_mod, release_package_number, release_package_number_mod, " +
                "sap_change_control, sap_change_control_mod, status, status_mod, title, title_mod, types_mod, contexts_mod, prerequisite_release_packages_mod) " +
                "VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        jdbcTemplate.update(stmt, releasePackageId, nextVal, 1, null, null, releasePackage.getChangeSpecialist3().getAbbreviation(), releasePackage.getChangeSpecialist3().getDepartmentName(), releasePackage.getChangeSpecialist3().getEmail(), releasePackage.getChangeSpecialist3().getFullName(), releasePackage.getChangeSpecialist3().getUserId(),
                false, releasePackage.getCreatedOn(), false, releasePackage.getCreator().getAbbreviation(), releasePackage.getCreator().getDepartmentName(), releasePackage.getCreator().getEmail(), releasePackage.getCreator().getFullName(), releasePackage.getCreator().getUserId(), false, releasePackage.getExecutor().getAbbreviation(),
                releasePackage.getExecutor().getDepartmentName(), releasePackage.getExecutor().getEmail(), releasePackage.getExecutor().getFullName(), releasePackage.getExecutor().getUserId(), false, releasePackage.getPlannedEffectiveDate(), false, releasePackage.getPlannedReleaseDate(), false,
                null, false, null, false, null, false, null, false, releasePackage.getReleasePackageNumber(), false, releasePackage.getSapChangeControl(), false, releasePackage.getStatus(), false, releasePackage.getTitle(), false, false, false, false);

        List<String> ListOfTags = getReleasePackageTags(releasePackageId);
        for(String tag:ListOfTags) {
            stmt = "INSERT INTO public.aud_tags( rev, id, tags, tags_order, revtype, revend, revend_tstmp) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(stmt, nextVal, releasePackageId, tag, ++i, 1, null, null);
        }

        return releasePackageId;

    }

    public Long createMyTeam(Long releasePackageId) {
        ReleasePackageMyTeam releasePackageMyTeam = EntityPojoFactory.createMyTeam(releasePackageId);
        long nextVal = getNextHibernateSequence();

        String stmt = "INSERT INTO public.my_team(id,dtype, release_package_id) VALUES (?, ?, ?)";

        jdbcTemplate.update(stmt, nextVal, "ReleasePackage", releasePackageMyTeam.getReleasePackage().getId());
        Long myTeamId = findMyTeamIdByReleasePackageId(releasePackageId);
        return myTeamId;
    }

    public Long createMyTeamMember(String dataIdentifier, Long myTeamId) {
        User myTeamMemberDetails = EntityPojoFactory.createMyTeamMember(dataIdentifier);
        long nextVal = getNextHibernateSequence();

        String stmt = "INSERT INTO public.my_team_member(id, abbreviation, department_name, email, full_name, user_id, myteam_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(stmt, nextVal, myTeamMemberDetails.getAbbreviation(), myTeamMemberDetails.getDepartmentName(),
                myTeamMemberDetails.getEmail(), myTeamMemberDetails.getFullName(), myTeamMemberDetails.getUserId(), myTeamId);
        Long myTeamMemberId = findMyTeamMemberIdByTeamId(myTeamId);
        stmt = "insert into aud_updater (abbreviation, department_name, email, full_name, " +
                "timestamp, user_id, id) values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt,
                null, null, null, null,
                (new Date()).getTime(), null, nextVal);

        stmt = "INSERT INTO public.aud_my_team_member(id,rev, revtype, revend, revend_tstmp, abbreviation, department_name, email, full_name, user_id, myteam_id)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(stmt, myTeamMemberId, nextVal, 1, null, null,  myTeamMemberDetails.getAbbreviation(), myTeamMemberDetails.getDepartmentName(),
                myTeamMemberDetails.getEmail(), myTeamMemberDetails.getFullName(), myTeamMemberDetails.getUserId(), myTeamId);
        return myTeamMemberId;
    }

    public Long addMemberRole(String role, Long id, Integer order) {
        String stmt = "INSERT INTO public.member_role(id, roles, roles_order) VALUES (?, ?, ?)";
        jdbcTemplate.update(stmt, id, role, order);
        Long memberRoleId = findMemberRoleByTeamId(id);
        return memberRoleId;
    }

    //creation of comment


    public Long createReleasePackageCommentAndSetStatus(String dataIdentifier, String properties, CommentStatus status, Long releasePackageId) {
        Long id = createReleasePackageComment(dataIdentifier, properties, releasePackageId);
        assert id != null;
        String stmt = "update comment set status = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, status.getStatusCode(), id);
        assert updatedRecords == 1;

        return id;
    }

    public Long createReleasePackageComment(String dataIdentifier, String properties, Long releasePackageId) {
        ReleasePackageComment releasePackageComment = null;
        releasePackageComment = EntityPojoFactory.createReleasePackageComment(dataIdentifier, properties);

        String stmt = "INSERT INTO public.comment(" +
                "dtype,created_on, creator_abbreviation, creator_department_name, creator_email, creator_full_name, " +
                "creator_user_id,comment_text,replyto_id,release_package_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, "ReleasePackage",
                releasePackageComment.getCreatedOn(), releasePackageComment.getCreator().getAbbreviation(), releasePackageComment.getCreator().getDepartmentName(),
                releasePackageComment.getCreator().getEmail(), releasePackageComment.getCreator().getFullName(), releasePackageComment.getCreator().getUserId(),
                releasePackageComment.getCommentText(), null, releasePackageId);
        long releasePackageCommentId = findReleasePackageCommentIdByCommentText(releasePackageComment.getCommentText());
        return releasePackageCommentId;
    }

    public Long findReleasePackageCommentIdByCommentText(String commentText) {
        String sql = "SELECT ID FROM COMMENT WHERE COMMENT_TEXT = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{commentText}, (rs, rowNum) ->
                rs.getLong("id"));

    }


    public Long createReleasePackageCommentDocumentAndSetStatus(String dataIdentifier, String properties, CommentStatus status,
                                                                Long releasePackageCommentId, DocumentStatus documentStatus) {
        Long id = createReleasePackageCommentDocument(dataIdentifier, properties, releasePackageCommentId);
        assert id != null;
        String stmt = "update document set status = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, documentStatus.getStatusCode(), id);
        assert updatedRecords == 1;

        return id;
    }

    public Long createReleasePackageCommentDocument(String dataIdentifier, String properties, Long releasePackageCommentId) {
        ReleasePackageCommentDocument releasePackageCommentDocument = null;
        releasePackageCommentDocument = EntityPojoFactory.createReleasePackageCommentDocument(dataIdentifier, properties);

        String stmt = "INSERT INTO public.document(" +
                "dtype,created_on, creator_abbreviation, creator_department_name, creator_email, creator_full_name, " +
                "creator_user_id,description,name,release_package_comment_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, "ReleasePackageCommentDocument",
                releasePackageCommentDocument.getCreatedOn(), releasePackageCommentDocument.getCreator().getAbbreviation(), releasePackageCommentDocument.getCreator().getDepartmentName(),
                releasePackageCommentDocument.getCreator().getEmail(), releasePackageCommentDocument.getCreator().getFullName(), releasePackageCommentDocument.getCreator().getUserId(),
                releasePackageCommentDocument.getDescription(), releasePackageCommentDocument.getName(), releasePackageCommentId);
        long releasePackageCommentDocumentId = findReleasePackageCommentDocumentIdByCommentId(releasePackageCommentId);
        return releasePackageCommentDocumentId;
    }

    public Long findReleasePackageCommentDocumentIdByCommentId(Long releasePackageCommentId) {
        String sql = "SELECT ID FROM DOCUMENT WHERE release_package_comment_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{releasePackageCommentId}, (rs, rowNum) ->
                rs.getLong("id"));

    }
    //RP document

    public Long createReleasePackageDocumentAndSetStatus(String dataIdentifier, String properties,
                                                         Long releasePackageId, DocumentStatus documentStatus) {
        Long id = createReleasePackageDocument(dataIdentifier, properties, releasePackageId);
        assert id != null;
        String stmt = "update document set status = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, documentStatus.getStatusCode(), id);
        assert updatedRecords == 1;

        return id;
    }

    public Long createReleasePackageDocument(String dataIdentifier, String properties, Long releasePackageId) {
        ReleasePackageDocument releasePackageDocument = null;
        releasePackageDocument = EntityPojoFactory.createReleasePackageDocument(dataIdentifier, properties);

        String stmt = "INSERT INTO public.document(" +
                "dtype,created_on, creator_abbreviation, creator_department_name, creator_email, creator_full_name, " +
                "creator_user_id,description,name,release_package_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt, "ReleasePackageDocument",
                releasePackageDocument.getCreatedOn(), releasePackageDocument.getCreator().getAbbreviation(), releasePackageDocument.getCreator().getDepartmentName(),
                releasePackageDocument.getCreator().getEmail(), releasePackageDocument.getCreator().getFullName(), releasePackageDocument.getCreator().getUserId(),
                releasePackageDocument.getDescription(), releasePackageDocument.getName(), releasePackageId);
        long releasePackageDocumentId = findReleasePackageDocumentIdByReleasePackageId(releasePackageId);
        return releasePackageDocumentId;
    }

    public Long findReleasePackageDocumentIdByReleasePackageId(Long releasePackageId) {
        String sql = "SELECT ID FROM DOCUMENT WHERE release_package_id = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{releasePackageId}, (rs, rowNum) ->
                rs.getLong("id"));

    }

    public void updateReleasePackageAndSetProjectId(Long releasePackageId, String projectId) {
        String stmt = "update release_package set project_id = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, projectId, releasePackageId);
        assert updatedRecords == 1;
    }

    public void updateReleasePackageAndSetProductId(Long releasePackageId, String productId) {
        String stmt = "update release_package set product_id = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, productId, releasePackageId);
        assert updatedRecords == 1;
    }

    public List<String> getReleasePackageTags(Long releasePackageId) {
        String sql = "SELECT TAGS FROM TAGS WHERE ID=?";
        return jdbcTemplate.query(sql, new Object[]{releasePackageId}, (rs, rowNum) ->
                rs.getString("tags"));
    }

    public String getReleasePackageNumberById(Long releasePackageId) {
        String sql = "SELECT release_package_number FROM release_package WHERE ID=?";
        return jdbcTemplate.queryForObject(sql, new Object[]{releasePackageId}, (rs, rowNum) ->
                rs.getString("release_package_number"));
    }

    public List<PrerequisiteReleasePackage> getAddPrerequisiteRequest(ReleasePackageStatus originalReleasePackageStatus) {

        int prerequisitesCount = 3;
        int sequence = 1;
        Long releasePackageId = 0L;
        String releasePackageNumber = null;
        List<PrerequisiteReleasePackage> prerequisiteReleasePackages = new ArrayList<>();
        for (int i = 0; i < prerequisitesCount; i++) {
            UUID uuid = UUID.randomUUID();
            String dataIdentifier = uuid.toString();
            releasePackageId = createReleasePackageAndSetStatus(dataIdentifier, "ALL_PROPERTIES", originalReleasePackageStatus);
            releasePackageNumber = getReleasePackageNumberById(releasePackageId);
            prerequisiteReleasePackages.add(new PrerequisiteReleasePackage(releasePackageId, releasePackageNumber, sequence));
            sequence = sequence + 1;
        }

        return prerequisiteReleasePackages;
    }

    public void createPrerequisites(Long id, List<PrerequisiteReleasePackage> inputPrerequisites) {

        int prerquisitesOrder = 0;
        for (PrerequisiteReleasePackage prerequisiteReleasePackage : inputPrerequisites) {
            String stmt = "INSERT INTO public.prerequisite_release_packages(" +
                    "id, release_package_id, release_package_number, sequence, prerequisite_release_packages_order) " +
                    "VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(stmt, id, prerequisiteReleasePackage.getReleasePackageId(), prerequisiteReleasePackage.getReleasePackageNumber(),
                    prerequisiteReleasePackage.getSequence(), prerquisitesOrder++);

        }
    }

    public String getECNById(Long id) {
        String sql = "SELECT context_id  FROM RELEASE_PACKAGE_CONTEXTS WHERE ID= ? and type='ECN'";
        return jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, rowNum) ->
                rs.getString("context_id"));
    }

    public void updateReleasePackageNumber(Long id) {
        String stmt = "update release_package set release_package_number = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, "117-01", id);
        assert updatedRecords == 1;
    }

    public void updateECN(Long id) {
        String stmt = "update release_package_contexts set context_id = ? where id = ? and type='ECN'";
        int updatedRecords = jdbcTemplate.update(stmt, "ECN-1010", id);
        assert updatedRecords == 1;
    }

    public List<String> getReleasePackagePrerequisites(Long releasePackageId) {
        String sql = "SELECT release_package_id FROM prerequisite_release_packages WHERE ID=?";
        return jdbcTemplate.query(sql, new Object[]{releasePackageId}, (rs, rowNum) ->
                rs.getString("release_package_id"));
    }

    public void updateReleasePackageNumberForOverviewByNumber(Long id) {
        String stmt = "update release_package set release_package_number = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, "43003-01", id);
        assert updatedRecords == 1;
    }


    public void updateECNForOverviewByEcn(Long id) {
        String stmt = "update release_package_contexts set context_id = ? where id = ? and type='ECN'";
        int updatedRecords = jdbcTemplate.update(stmt, "ECN-600501", id);
        assert updatedRecords == 1;
    }

    public void updateReleasePackageNumberForSearchSummary(Long id) {
        String stmt = "update release_package set release_package_number = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, "43003-16", id);
        assert updatedRecords == 1;
    }


    public Long createReleasePackageAndSetStatusForCaseAction(String dataIdentifier, String properties, ReleasePackageStatus status,ReleasePackageCaseActions releasePackageCaseActions,String changeOwnerType,boolean sapChangeControl) {
        Long id = createReleasePackageForCaseAction(dataIdentifier, properties,releasePackageCaseActions,changeOwnerType,sapChangeControl);
        Long myteamId = createMyTeam(id);
        assert myteamId != null;
        assert id != null;
        String stmt = "update release_package set status = ? where id = ?";
        int updatedRecords = jdbcTemplate.update(stmt, status.getStatusCode(), id);
        assert updatedRecords == 1;

        return id;
    }

    public Long createReleasePackageForCaseAction(String dataIdentifier, String properties,ReleasePackageCaseActions releasePackageCaseActions,String changeOwnerType,boolean sapChangeControl ) {
        ReleasePackage releasePackage = null;
        releasePackage = EntityPojoFactory.createReleasePackageForCaseAction(dataIdentifier, properties,releasePackageCaseActions);
        releasePackage.setChangeOwnerType(changeOwnerType);
        releasePackage.setSapChangeControl(sapChangeControl);
        //call existing method
        //update CO context status to 2,3,4
        String stmt = "INSERT INTO public.release_package(" +
                "change_specialist_3_abbreviation, change_specialist_3_department_name, change_specialist_3_email, change_specialist_3_full_name, change_specialist_3_user_id, created_on, creator_abbreviation, creator_department_name, creator_email, creator_full_name, creator_user_id, executor_abbreviation, executor_department_name, executor_email, executor_full_name, executor_user_id, planned_effective_date, planned_release_date, prerequisites_applicable, prerequisites_detail, product_id, " +
                "project_id, release_package_number, sap_change_control, status, title, is_secure, plm_coordinator_user_id,change_owner_type) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
        jdbcTemplate.update(stmt, releasePackage.getChangeSpecialist3().getAbbreviation(), releasePackage.getChangeSpecialist3().getDepartmentName(), releasePackage.getChangeSpecialist3().getEmail(), releasePackage.getChangeSpecialist3().getFullName(), releasePackage.getChangeSpecialist3().getUserId(),
                releasePackage.getCreatedOn(), releasePackage.getCreator().getAbbreviation(), releasePackage.getCreator().getDepartmentName(), releasePackage.getCreator().getEmail(), releasePackage.getCreator().getFullName(), releasePackage.getCreator().getUserId(),
                releasePackage.getExecutor().getAbbreviation(), releasePackage.getExecutor().getDepartmentName(), releasePackage.getExecutor().getEmail(), releasePackage.getExecutor().getFullName(), releasePackage.getExecutor().getUserId(), releasePackage.getPlannedEffectiveDate(),
                releasePackage.getPlannedReleaseDate(), null, null, null, null, releasePackage.getReleasePackageNumber(), releasePackage.getSapChangeControl(), releasePackage.getStatus(), releasePackage.getTitle(), false, releasePackage.getPlmCoordinator().getUserId(),releasePackage.getChangeOwnerType());

        long releasePackageId = findByReleasePackageId(releasePackage.getReleasePackageNumber());

        long nextVal = getNextHibernateSequence();
        int order = 0;
        for (ReleasePackageContext releasePackageContext : releasePackage.getContexts()) {
            stmt = "INSERT INTO release_package_contexts(" +
                    "id, context_id, name, type, status, contexts_order) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(stmt,
                    releasePackageId, releasePackageContext.getContextId(), releasePackageContext.getName(), releasePackageContext.getType(), releasePackageContext.getStatus(), order++);
        }

        List<String> tags = EntityPojoFactory.addReleasePackageTags(dataIdentifier);
        int i=0;
        for(String tag : tags) {
            stmt = "INSERT INTO public.tags(id, tags, tags_order) VALUES (?, ?, ?)";
            jdbcTemplate.update(stmt, releasePackageId, tag, i++);
        }

        List<String> types = EntityPojoFactory.addReleasePackageTypes();
        int j=0;
        for(String type : types) {
            stmt = "INSERT INTO public.types(id, types, types_order) VALUES (?, ?, ?)";
            jdbcTemplate.update(stmt, releasePackageId, type, j++);
        }

        stmt = "insert into aud_updater (abbreviation, department_name, email, full_name, " +
                "timestamp, user_id, id) values (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(stmt,
                releasePackage.getCreator().getAbbreviation(), releasePackage.getCreator().getDepartmentName(), releasePackage.getCreator().getEmail(), releasePackage.getCreator().getFullName(),
                (new Date()).getTime(), releasePackage.getCreator().getUserId(), nextVal);

        stmt = "INSERT INTO public.aud_release_package" +
                "(id,rev, revtype, revend, revend_tstmp, change_specialist_3_abbreviation, change_specialist_3_department_name, change_specialist_3_email, change_specialist_3_full_name, change_specialist_3_user_id, " +
                "change_specialist3_mod, created_on, created_on_mod, creator_abbreviation, creator_department_name, creator_email, creator_full_name, creator_user_id, creator_mod, executor_abbreviation, " +
                "executor_department_name, executor_email, executor_full_name, executor_user_id, executor_mod, planned_effective_date, planned_effective_date_mod, planned_release_date, planned_release_date_mod, " +
                "prerequisites_applicable, prerequisites_applicable_mod, prerequisites_detail, prerequisites_detail_mod, product_id, product_id_mod, project_id, project_id_mod, release_package_number, release_package_number_mod, " +
                "sap_change_control, sap_change_control_mod, status, status_mod, title, title_mod, types_mod, contexts_mod, prerequisite_release_packages_mod) " +
                "VALUES (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

        jdbcTemplate.update(stmt, releasePackageId, nextVal, 1, null, null, releasePackage.getChangeSpecialist3().getAbbreviation(), releasePackage.getChangeSpecialist3().getDepartmentName(), releasePackage.getChangeSpecialist3().getEmail(), releasePackage.getChangeSpecialist3().getFullName(), releasePackage.getChangeSpecialist3().getUserId(),
                false, releasePackage.getCreatedOn(), false, releasePackage.getCreator().getAbbreviation(), releasePackage.getCreator().getDepartmentName(), releasePackage.getCreator().getEmail(), releasePackage.getCreator().getFullName(), releasePackage.getCreator().getUserId(), false, releasePackage.getExecutor().getAbbreviation(),
                releasePackage.getExecutor().getDepartmentName(), releasePackage.getExecutor().getEmail(), releasePackage.getExecutor().getFullName(), releasePackage.getExecutor().getUserId(), false, releasePackage.getPlannedEffectiveDate(), false, releasePackage.getPlannedReleaseDate(), false,
                null, false, null, false, null, false, null, false, releasePackage.getReleasePackageNumber(), false, releasePackage.getSapChangeControl(), false, releasePackage.getStatus(), false, releasePackage.getTitle(), false, false, false, false);

        List<String> ListOfTags = getReleasePackageTags(releasePackageId);
        for(String tag:ListOfTags) {
            stmt = "INSERT INTO public.aud_tags( rev, id, tags, tags_order, revtype, revend, revend_tstmp) VALUES (?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(stmt, nextVal, releasePackageId, tag, ++i, 1, null, null);
        }

        //EntityPojoFactory.addReleasePackageContextChangeObject(releasePackage,dataIdentifier,releasePackageCaseActions);
        return releasePackageId;

    }
}

