package com.example.mirai.projectname.changerequestservice.changerequest.repository;

import com.example.mirai.libraries.entity.repository.BaseRepository;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import com.example.mirai.projectname.changerequestservice.changerequest.model.dto.ActionsCountPerStatus;
import com.example.mirai.projectname.changerequestservice.changerequest.model.dto.ChangeBoardRule;
import com.example.mirai.projectname.changerequestservice.changerequest.model.dto.StatusCountByPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, Long>,
        JpaSpecificationExecutor<ChangeRequest>, BaseRepository<ChangeRequest> {
    @Query("SELECT count(c.id) FROM ChangeRequest c join c.contexts co  WHERE co.type=?1 AND c.status IN ?2 AND co.status IN ?3")
    Integer getActionData(String contextType, List<Integer> changeRequestStatuses, List<String> contextStatuses);
    @Query("SELECT count(c.id) FROM ChangeRequest c join c.contexts co  WHERE co.type=?1 AND c.status IN ?2")
    Integer getAllActionData(String contextType, List<Integer> changeRequestStatuses);
    @Query("SELECT count(c.id) as count, c.status as status, c.analysisPriority as analysisPriority FROM ChangeRequest c WHERE c.id IN ?1 group by c.status,c.analysisPriority")
    List<StatusCountByPriority[]> getStatusCountByAnalysisPriority(List<Long> ids);
    @Query("SELECT count(c.id) FROM ChangeRequest c WHERE c.id IN ?1 and c.status=?2")
    Long getChangeRequestCountByStatus(List<Long> ids, Integer status);

    @Query("SELECT a.changeRequestStatus AS changeRequestStatus, sum(a.openActions) AS openActions, sum(a.completedActions) AS completedActions, sum(a.openActions + a.completedActions) as totalActions FROM OverviewActions a WHERE a.changeRequestId IN ?1 GROUP BY changeRequestStatus")
    List<ActionsCountPerStatus> getChangeRequestActionsByStatus(List<Long> ids);

    @Query("SELECT c.id AS changeRequestId, c.changeBoardRuleSet.ruleSetName AS ruleSetName, r AS changeBoardRule from ChangeRequest c JOIN c.changeBoardRuleSet.rules r WHERE c.id IN ?1")
    List<ChangeBoardRule> getChangeBoardRulesByIds(List<Long> ids);
}
