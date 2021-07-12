package com.example.mirai.projectname.changerequestservice.shared.util;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum AnalysisPriorityValues {
    CRITICAL(1, "1 - Critical"),HIGH(2,  "2 - High"),MEDIUM(3, "3 - Medium"),LOW(4, "4 - Low");

    private Integer priority;
    private String label;
    AnalysisPriorityValues(Integer priority, String label) {
        this.priority = priority;
        this.label = label;
    }

    public static String getLabelByCode(Integer priority) {
        Optional<AnalysisPriorityValues> analysisPriorityValue = Arrays.stream(AnalysisPriorityValues.values()).filter(status -> status.getPriority().equals(priority)).findFirst();
        if (analysisPriorityValue.isPresent())
            return analysisPriorityValue.get().getLabel();
        return "";
    }


}
