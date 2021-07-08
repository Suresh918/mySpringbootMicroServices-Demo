package com.example.mirai.libraries.myteam.model.dto;


import java.util.Map;

import com.example.mirai.libraries.core.model.AggregateInterface;
import com.example.mirai.libraries.myteam.model.MyTeamMember;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MyTeamPublishData implements AggregateInterface {
	private Map<String, AggregateInterface> relatedEntity;

	private MyTeamMember myTeamMember;
}
