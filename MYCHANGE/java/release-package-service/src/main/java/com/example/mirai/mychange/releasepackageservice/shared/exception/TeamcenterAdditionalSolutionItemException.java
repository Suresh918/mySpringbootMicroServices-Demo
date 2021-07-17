package com.example.mirai.projectname.releasepackageservice.shared.exception;

import java.util.List;

import lombok.Getter;

@Getter
public class TeamcenterAdditionalSolutionItemException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String message;
    public TeamcenterAdditionalSolutionItemException(List<String> items){
        String message = ReleasePackageErrorStatusCodes.ADDITIONAL_SOLUTION_ITEM_IN_TEAMCENTER.getMessage();
        this.message = message.replace("<ITEMS>" , String.join(" <br> ", items));
    }
}
