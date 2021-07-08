package com.example.mirai.libraries.myteam.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.core.model.BaseEntityInterface;
import com.example.mirai.libraries.core.model.EntityLink;
import com.example.mirai.libraries.entity.controller.EntityController;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.model.dto.Member;
import com.example.mirai.libraries.myteam.model.dto.MyTeamBulkUpdate;
import com.example.mirai.libraries.myteam.service.MyTeamService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class MyTeamEntityController extends EntityController {

	public MyTeamEntityController(ObjectMapper objectMapper,
			MyTeamService myTeamService, EntityResolverDefaultInterface entityResolver) {
		super(objectMapper, myTeamService, entityResolver);
	}

	MyTeamService getService() {
		return ((MyTeamService) (super.entityServiceDefaultInterface));
	}

	@GetMapping(value = "/users")
	public List<Member> getUserRoleDetails(@RequestParam(name = "ids") String[] userIds) {
		return getService().getUserRoleDetails(userIds);
	}

	@PutMapping("/{id}/my-team-members")
	public Member addMember(@PathVariable String parentType,
			@PathVariable String entityType,
			@PathVariable Long id,
			@RequestBody JsonNode jsonNode) throws JsonProcessingException {
		MyTeamMember myTeamMember = objectMapper.treeToValue(jsonNode, MyTeamMember.class);
		Class<? extends BaseEntityInterface> parentEntityClass = this.entityResolverDefaultInterface.getEntityClass(parentType, entityType);
		EntityLink entityLink = new EntityLink(id, parentEntityClass);
		Set<EntityLink<BaseEntityInterface>> entityLinkSet = new HashSet<>();
		entityLinkSet.add(entityLink);
		return getService().addMember(myTeamMember, entityLinkSet);
	}

	@DeleteMapping("/my-team-members/{id}")
	public void removeMember(@PathVariable String parentType,
			@PathVariable String entityType,
			@PathVariable Long id) {
		getService().deleteMyTeamMember(parentType, entityType, id);
	}

	@SneakyThrows
	@PatchMapping("/my-team-members/{id}")
	public MyTeamMember mergeMember(@PathVariable Long id, @RequestBody JsonNode jsonNode) {
		BaseEntityInterface oldIns = this.objectMapper.treeToValue(jsonNode.get("oldIns"), MyTeamMember.class);
		BaseEntityInterface newIns = this.objectMapper.treeToValue(jsonNode.get("newIns"), MyTeamMember.class);
		oldIns.setId(id);
		newIns.setId(id);
		List<String> oldInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("oldIns"));
		List<String> newInsChangedAttributeNames = ObjectMapperUtil.getChangedAttributeNames(jsonNode.get("newIns"));
		return getService().mergeMyTeamMember(newIns, oldIns, oldInsChangedAttributeNames, newInsChangedAttributeNames);
	}

	@GetMapping(value = { "/id" }, params = { "view=aggregate" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public AggregateInterface getAggregate(@PathVariable String parentType, @PathVariable String entityType, @PathVariable Long id) {
		Class aggregateClass = this.entityResolverDefaultInterface.getAggregateClass(parentType, entityType);
		return getService().getAggregate(id, aggregateClass);
	}

	@PatchMapping(value = "/my-team-members", params = {"case-action", "is-all-selected"})
	public void bulkUpdateMyTeamMembers(@RequestBody JsonNode jsonNode, @RequestParam("case-action") String myTeamBulkAction,
										@RequestParam("is-all-selected") Boolean isAllSelected) {
		MyTeamBulkUpdate myTeamBulkUpdate = objectMapper.convertValue(jsonNode, MyTeamBulkUpdate.class);
		getService().bulkUpdateMyTeamMembers(myTeamBulkUpdate, myTeamBulkAction, isAllSelected);

	}
}
