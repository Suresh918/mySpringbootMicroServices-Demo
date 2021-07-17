package com.example.mirai.projectname.releasepackageservice.json;


import com.example.mirai.libraries.entity.util.ObjectMapperUtil;
import net.minidev.json.JSONArray;

import java.util.Arrays;

public class MyTeamCasePermissionJson extends Content {
    public MyTeamCasePermissionJson(String content) {
        super(content);
    }

    public JSONArray getIsAllowedForCaseActionForMyTeam(String caseAction) {
        return documentContext.read("$.case_actions[?(@.case_action=='" + caseAction + "')].is_allowed");
    }

    public String getIsAllowedFlag(String caseAction) {
        JSONArray jsonArray = this.getIsAllowedForCaseActionForMyTeam(caseAction);
        String[] flag = ObjectMapperUtil.getObjectMapper().convertValue(jsonArray, String[].class);
        return Arrays.stream(flag).findFirst().get();
    }
}
