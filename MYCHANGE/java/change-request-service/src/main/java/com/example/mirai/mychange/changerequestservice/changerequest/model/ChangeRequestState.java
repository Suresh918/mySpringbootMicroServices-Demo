package com.example.mirai.projectname.changerequestservice.changerequest.model;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Optional;

@Getter
@Slf4j
public enum ChangeRequestState {
    CREATE(new Integer[]{1}, "Create"), DEFINE_SOLUTION(new Integer[]{2}, "Define Solution"),
    ANALYZE_IMPACT(new Integer[]{3}, "Analyze Impact"), DECIDE(new Integer[]{4, 5}, "Decide"),
    CLOSE(new Integer[]{6,7}, "Close");

    private Integer[] statuses;
    private String stateLabel;
    ChangeRequestState(Integer[] statuses, String stateLabel) {
        this.stateLabel = stateLabel;
        this.statuses = statuses;
    }

    public static String getStateByStatus(Integer status) {
        Optional<ChangeRequestState> changeRequestState = Arrays.stream(ChangeRequestState.values()).filter(state -> Arrays.asList(state.getStatuses()).contains(status)).findFirst();
        if (changeRequestState.isPresent())
            return changeRequestState.get().name();
        log.info("Status " +status+ " not mapped in State");
        return "";
    }
}
