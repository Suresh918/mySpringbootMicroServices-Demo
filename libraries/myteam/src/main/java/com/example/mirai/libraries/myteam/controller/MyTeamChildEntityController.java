package com.example.mirai.libraries.myteam.controller;

import java.util.List;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.entity.controller.ChildEntityController;
import com.example.mirai.libraries.entity.service.EntityResolverDefaultInterface;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.libraries.myteam.service.MyTeamService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class MyTeamChildEntityController extends ChildEntityController {
	public MyTeamChildEntityController(ObjectMapper objectMapper,
			MyTeamService myTeamService, EntityResolverDefaultInterface entityResolver) {
		super(objectMapper, myTeamService, entityResolver);
	}

	MyTeamService getService() {
		return ((MyTeamService) (super.entityServiceDefaultInterface));
	}

	@SneakyThrows
	@GetMapping("/my-team-members")
	@ResponseStatus(HttpStatus.OK)
	public List<MyTeamMember> getAllMembersOfMyTeam(@PathVariable Long parentId) {
		return getService().getAllMembersOfMyTeam(parentId);
	}

	@GetMapping(params = { "view=aggregate" })
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public AggregateInterface getAggregateByParent(@PathVariable String parentType, @PathVariable String entityType, @PathVariable Long parentId) {
		Class aggregateClass = this.entityResolverDefaultInterface.getAggregateClass(parentType, entityType);
		return getService().getAggregateByParent(parentId, aggregateClass);
	}

}
