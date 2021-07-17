package com.example.mirai.projectname.notificationservice.engine.processor.changerequest;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@Getter
public enum PriorityValues {
    CRITICAL(1, "1 - Critical"),HIGH(2, "2 - High"),MEDIUM(3, "3 - Medium"),LOW(4, "4 - Low");

    private Integer priorityCode;
    private String priority;
    PriorityValues(Integer priorityCode, String priority) {
        this.priorityCode = priorityCode;
        this.priority = priority;
    }

    public static String getPriorityByCode(Integer code) {
        Optional<PriorityValues> priorityValue = Arrays.stream(PriorityValues.values()).filter(item -> code.equals(item.getPriorityCode())).findFirst();
        if (priorityValue.isEmpty()) {
            return "";
        }
        return priorityValue.get().getPriority();
    }

}
