package com.example.mirai.projectname.releasepackageservice.tests.myteams;

import com.example.mirai.libraries.myteam.model.MyTeamMember;
import com.example.mirai.projectname.releasepackageservice.ExceptionValidator;
import com.example.mirai.projectname.releasepackageservice.json.ExceptionResponse;
import com.example.mirai.projectname.releasepackageservice.json.ReleasePackageMyTeamJson;
import com.example.mirai.projectname.releasepackageservice.myteam.model.ReleasePackageMyTeam;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Validator {

    public static void createMyTeamIsSuccessful(ReleasePackageMyTeam releasePackageMyTeamSaved, ReleasePackageMyTeamJson responseReleasePackageMyTeamJson) {
        assertThat("response id is null", responseReleasePackageMyTeamJson.getId(), notNullValue());
        assertThat("id are not same", releasePackageMyTeamSaved.getId(), equalTo(responseReleasePackageMyTeamJson.getId()));
    }

    public static void myTeamIsEqual(ReleasePackageMyTeam releasePackageMyTeam1, ReleasePackageMyTeam releasePackageMyTeam2) {
        assertThat("id are not same", releasePackageMyTeam1.getId(), equalTo(releasePackageMyTeam2.getId()));
        assertThat("id are not same", releasePackageMyTeam1.getReleasePackage(), samePropertyValuesAs(releasePackageMyTeam2.getReleasePackage()));
    }

    public static void createMyTeamMemberIsSuccessful(MyTeamMember myTeamMember1, MyTeamMember myTeamMember2, ReleasePackageMyTeamJson releasePackageMyTeamJson) {
        assertThat("release package are not same", ((ReleasePackageMyTeam) myTeamMember1.getMyteam()).getReleasePackage(), samePropertyValuesAs(((ReleasePackageMyTeam) myTeamMember2.getMyteam()).getReleasePackage()));
        assertThat("my team id are not same", myTeamMember1.getMyteam().getId(), equalTo(myTeamMember2.getMyteam().getId()));
        assertThat("my team are not same", myTeamMember1.getStatus(), equalTo(myTeamMember2.getStatus()));
        assertThat("roles are not same", myTeamMember1.getRoles(), equalTo(myTeamMember2.getRoles()));
        assertThat("my team member id is not null", myTeamMember2.getId(), notNullValue());
    }

    public static void myTeamMemberIsEqualWithoutRoles(MyTeamMember myTeamMember1, MyTeamMember myTeamMember2) {
        assertThat("release package are not same", ((ReleasePackageMyTeam) myTeamMember1.getMyteam()).getReleasePackage(), samePropertyValuesAs(((ReleasePackageMyTeam) myTeamMember2.getMyteam()).getReleasePackage()));
        assertThat("my team member id are not same", myTeamMember1.getId(), equalTo(myTeamMember2.getId()));
        assertThat("my team id are not same", myTeamMember1.getMyteam().getId(), equalTo(myTeamMember2.getMyteam().getId()));
        assertThat("my team status are not same", myTeamMember1.getMyteam().getStatus(), equalTo(myTeamMember2.getMyteam().getStatus()));
        assertThat("my team are not same", myTeamMember1.getStatus(), equalTo(myTeamMember2.getStatus()));
    }

    public static void updateTeamMemberRoleIsSuccessful(MyTeamMember myTeamMember1, MyTeamMember myTeamMember2) {
        myTeamMemberIsEqualWithoutRoles(myTeamMember1, myTeamMember2);
        assertThat("roles are same", myTeamMember1.getRoles(), not(myTeamMember2.getRoles()));
    }

    public static void updateTeamMemberRoleIsUnSuccessful(MyTeamMember myTeamMember1, MyTeamMember myTeamMember2) {
        myTeamMemberIsEqualWithoutRoles(myTeamMember1, myTeamMember2);
        assertThat("roles are not same", myTeamMember1.getRoles(), equalTo(myTeamMember2.getRoles()));
    }

    public static void caseActionAllowed(ReleasePackageMyTeam releasePackageMyTeam1, ReleasePackageMyTeam releasePackageMyTeam2, String expectedResult, String isCaseActionAllowed) {
        myTeamIsEqual(releasePackageMyTeam1, releasePackageMyTeam2);
        assertThat("case action are not same", expectedResult, equalTo(isCaseActionAllowed));
    }

    public static void unauthorizedExceptionAndMyTeamMemberDidNotChange(MyTeamMember myTeamMemberBeforeDelete, MyTeamMember myTeamMemberAfterDelete, ExceptionResponse exceptionResponse,
                                                                        String path) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        myTeamMemberIsEqualWithoutRoles(myTeamMemberBeforeDelete, myTeamMemberAfterDelete);
        assertThat("roles are not same", myTeamMemberBeforeDelete.getRoles(), equalTo(myTeamMemberAfterDelete.getRoles()));
    }
}
