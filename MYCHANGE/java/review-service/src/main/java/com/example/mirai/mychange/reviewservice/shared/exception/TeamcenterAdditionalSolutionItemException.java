package com.example.mirai.projectname.reviewservice.shared.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class TeamcenterAdditionalSolutionItemException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private String message;
    public TeamcenterAdditionalSolutionItemException(List<String> items){
        String message = ReviewErrorStatusCodes.ADDITIONAL_SOLUTION_ITEM_IN_TEAMCENTER.getMessage();
        this.message = message.replace("<ITEMS>" , String.join(" <br> ", items));
    }
}
