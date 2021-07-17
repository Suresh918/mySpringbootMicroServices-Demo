package com.example.mirai.projectname.releasepackageservice.tests.releasepackage;

import com.example.mirai.projectname.releasepackageservice.ExceptionValidator;
import com.example.mirai.projectname.releasepackageservice.json.ExceptionResponse;
import com.example.mirai.projectname.releasepackageservice.json.ReleasePackageJson;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackage;
import com.example.mirai.projectname.releasepackageservice.releasepackage.model.ReleasePackageContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class Validator {

    public static void releasePackagesAreSameWithoutComparingAuditAndStatus(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        assertThat("titles are not same", releasePackage1.getTitle(), equalTo(releasePackage2.getTitle()));
        assertThat("planned effective date are not same", releasePackage1.getPlannedEffectiveDate(), equalTo(releasePackage2.getPlannedEffectiveDate()));
        assertThat("planned release date are not same", releasePackage1.getPlannedReleaseDate(), equalTo(releasePackage2.getPlannedReleaseDate()));
        assertThat("sap change control are not same", releasePackage1.getSapChangeControl(), equalTo(releasePackage2.getSapChangeControl()));
        assertThat("project id are not same", releasePackage1.getProjectId(), equalTo(releasePackage2.getProjectId()));
        assertThat("product id are not same", releasePackage1.getProductId(), equalTo(releasePackage2.getProductId()));
        assertThat("prerequisites detail are not same", releasePackage1.getPrerequisitesDetail(), equalTo(releasePackage2.getPrerequisitesDetail()));

        assertThat("executors is null", releasePackage1.getExecutor(), is(notNullValue()));
        assertThat("executors are not same", releasePackage1.getExecutor(), samePropertyValuesAs(releasePackage2.getExecutor()));

        assertThat("changeSpecialist3 is null", releasePackage1.getChangeSpecialist3(), is(notNullValue()));
        assertThat("changeSpecialist3 are not same", releasePackage1.getChangeSpecialist3(), samePropertyValuesAs(releasePackage2.getChangeSpecialist3()));

        long releasePackageContextCount = releasePackage1.getContexts().stream().filter(context -> context.getType().equals("CHANGENOTICE")).count();
        assertThat("number of releasepackage contexts is not equal to 1", releasePackageContextCount, is(equalTo(1L)));
        ReleasePackageContext releasePackageContext1 = releasePackage1.getContexts().stream().filter(context -> context.getType().equals("CHANGENOTICE")).findFirst().get();
        ReleasePackageContext releasePackageContext2 = releasePackage2.getContexts().stream().filter(context -> context.getType().equals("CHANGENOTICE")).findFirst().get();
        assertThat("releasepackage contexts are not same", releasePackageContext1, samePropertyValuesAs(releasePackageContext2));
    }

    public static void releasePackagesAreSame(ReleasePackage releasePackage1, ReleasePackage releasePackage2) {
        releasePackagesAreSameWithoutComparingAuditAndStatus(releasePackage1, releasePackage2);
        assertThat("created_on is null", releasePackage1.getCreatedOn(), is(notNullValue()));
        assertThat("created_on dates are not same", releasePackage1.getCreatedOn(), is(releasePackage2.getCreatedOn()));

        assertThat("creator is null", releasePackage1.getCreator(), is(notNullValue()));
        assertThat("creators are not same", releasePackage1.getCreator(), samePropertyValuesAs(releasePackage2.getCreator()));

        assertThat("statuses are not same", releasePackage1.getStatus(), equalTo(releasePackage2.getStatus()));
    }

    public static void unauthorizedExceptionAndReleasePackageDidNotChange(ReleasePackage releasePackageBeforeCaseAction, ReleasePackage releasePackageAfterCaseAction,
                                                                          ExceptionResponse exceptionResponse,
                                                                          String path, Integer originalStatus) {
        ExceptionValidator.exceptionResponseIsUnauthorizedException(exceptionResponse, path);
        releasePackagesAreSame(releasePackageBeforeCaseAction, releasePackageAfterCaseAction);
        assertThat("unauthorized case action has changed status", releasePackageAfterCaseAction.getStatus(), equalTo(originalStatus));
    }

    public static void createReleasePackageIsSuccessful(ReleasePackage releasePackageRequest, ReleasePackage releasePackageSaved,
                                                        ReleasePackageJson responseReleasePackage) {
        releasePackagesAreSameWithoutComparingAuditAndStatus(releasePackageRequest, releasePackageSaved);
        assertThat("id are not same", responseReleasePackage.getId(), equalTo(releasePackageSaved.getId()));
        assertThat("created_on is null", responseReleasePackage.getCreatedOn(), is(notNullValue()));
        assertThat("created_on dates are not same", responseReleasePackage.getCreatedOn(), is(releasePackageSaved.getCreatedOn()));
        assertThat("statuses are not same", responseReleasePackage.getStatus(), equalTo(releasePackageSaved.getStatus()));

    }
}
