package com.example.mirai.projectname.notificationservice.engine.processor.changerequest;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;

import java.util.List;
import java.util.Map;

public class ChangeRequestPropertyExtractorUtil {

    public static Object getChangeRequestIdFromChangeRequestAggregate(Map data) {
        if (data == null)
            return null;
        return JsonPath.parse(data).read("$.description.id");
    }

    public static Map getChangeSpecialist1FromChangeRequestAggregate(Map data) {
        if (data == null)
            return null;
        return JsonPath.parse(data).read("$.description.change_specialist1");
    }

    public static List getChangeRequestMyTeamMembersFromChangeRequestAggregate(Map data) {
        if (data == null)
            return new JSONArray();
        return JsonPath.parse(data).read("$.my_team_details.members");
    }

    public static Map getUserFromMember(Map memberData) {
        if (memberData == null)
            return null;
        return JsonPath.parse(memberData).read("$.member.user");
    }
}
