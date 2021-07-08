package com.example.mirai.libraries.myteam.controller;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.myteam.service.MyTeamService;
import com.example.mirai.libraries.security.core.controller.SecurityController;

import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class MyTeamSecurityController extends SecurityController {

	public MyTeamSecurityController(MyTeamService myTeamService) {
		super(myTeamService);
	}

	@Override
	public Class<AggregateInterface> getCaseStatusAggregateClass() {
		return null;
	}
}
