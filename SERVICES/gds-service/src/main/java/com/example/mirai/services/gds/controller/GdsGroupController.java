package com.example.mirai.services.gds.controller;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import com.example.mirai.libraries.core.model.Group;
import com.example.mirai.services.gds.service.GdsGroupService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/groups")
public class GdsGroupController {
	private final GdsGroupService gdsGroupService;

	public GdsGroupController(GdsGroupService gdsGroupService) {
		this.gdsGroupService = gdsGroupService;
	}

	@GetMapping("/{group_id}")
	public Group getGroupByGroupId(@PathVariable(name = "group_id") @NotBlank @Size(min = 5, max = 40) String groupId) {
		return gdsGroupService.getGroupByGroupId(groupId);
	}

	@GetMapping(params = "group_id")
	public Object getGroupsByGroupIds(@RequestParam(name = "group_id") @NotEmpty @Size(max = 20) List<String> groupIds) {
		List<Group> groups = gdsGroupService.getGroupsByGroupIds(groupIds);
		if (groups == null || groups.isEmpty())
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else
			return groups;
	}

	@GetMapping(params = { "group_id_prefix", "q" })
	public Object findPrefixedGroup(
			@RequestParam(name = "group_id_prefix") @NotBlank @Size(min = 3, max = 20) String groupIdPrefix,
			@RequestParam(name = "q") @NotBlank @Size(min = 3, max = 40) String searchQuery
	) {
		List<Group> groups = gdsGroupService.findPrefixedGroup(groupIdPrefix, searchQuery);
		if (groups == null || groups.isEmpty())
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else
			return groups;
	}

	@GetMapping(params = { "q" })
	public Object findGroup(
			@RequestParam(name = "q") @NotBlank @Size(min = 3, max = 40) String searchQuery
	) {
		List<Group> groups = gdsGroupService.findGroup(searchQuery);
		if (groups == null || groups.isEmpty())
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else
			return groups;
	}
}
