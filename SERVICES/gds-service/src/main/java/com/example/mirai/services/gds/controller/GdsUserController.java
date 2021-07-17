package com.example.mirai.services.gds.controller;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.example.mirai.libraries.core.model.User;
import com.example.mirai.services.gds.service.GdsUserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/users")
public class GdsUserController {
	private final GdsUserService gdsUserService;

	public GdsUserController(GdsUserService gdsUserService) {
		this.gdsUserService = gdsUserService;
	}

	@GetMapping("/{user_id}")
	@ResponseBody
	public Object getUserByUserId(@PathVariable(name = "user_id") @NotBlank @Size(min = 3, max = 20) String userId) {
		return gdsUserService.getUserByUserId(userId);
	}

	@GetMapping(params = { "abbreviation" })
	public User getUserByAbbreviation(
			@RequestParam(name = "abbreviation") @NotBlank @Size(min = 4, max = 4) String abbreviation
	) {
		return gdsUserService.getUserByAbbreviation(abbreviation);
	}

	@GetMapping(params = { "q" })
	public Object findUser(
			@RequestParam(name = "q") @NotBlank @Size(min = 3, max = 40) String searchQuery
	) {
		List<User> users = gdsUserService.findUser(searchQuery);
		if (users == null || users.isEmpty())
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else
			return users;
	}
}
