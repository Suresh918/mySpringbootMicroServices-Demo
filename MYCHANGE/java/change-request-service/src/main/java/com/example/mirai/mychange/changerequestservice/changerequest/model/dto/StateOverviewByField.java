package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestState;
import com.example.mirai.projectname.changerequestservice.shared.util.AnalysisPriorityValues;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StateOverviewByField {
    private List<ChangeRequestStateCountByField> stateCounts = new ArrayList();

    public StateOverviewByField(List<ChangeRequestStatusCountByFieldValue> statusCounts) {
        Arrays.stream(ChangeRequestState.values()).forEach(state -> {
            if (!state.name().equals("CLOSE"))
                stateCounts.add(new ChangeRequestStateCountByField(state, statusCounts));
        });
    }

    @Getter
    @Setter
    private class ChangeRequestStateCountByField {
        private String name;
        private String label;
        private Long value;
        private List<FieldValueCount> series;

        private ChangeRequestStateCountByField(ChangeRequestState changeRequestState, List<ChangeRequestStatusCountByFieldValue> statusCountsByAnalysisPriority) {
            this.name = changeRequestState.name();
            this.label = changeRequestState.getStateLabel();
            this.series = Arrays.stream(AnalysisPriorityValues.values()).map(item -> new FieldValueCount(item.getPriority(), 0L)).collect(Collectors.toList());
            //for null value
            this.series.add(new FieldValueCount(-2, 0L));
            List<ChangeRequestStatusCountByFieldValue> statusCounts = statusCountsByAnalysisPriority.stream()
                    .filter(statusCount -> Arrays.asList(changeRequestState.getStatuses()).contains(statusCount.getStatus())
                    ).collect(Collectors.toList());
            for (ChangeRequestStatusCountByFieldValue statusCount : statusCounts) {
                for (FieldValueCount fieldValueCount : this.series) {
                    if (Objects.isNull(statusCount.getTypeCount()) && fieldValueCount.getName() == -2) {
                        fieldValueCount.setValue(fieldValueCount.getValue() + statusCount.getCount());
                    }
                    if (fieldValueCount.getName().equals(statusCount.getTypeCount())) {
                        fieldValueCount.setValue(fieldValueCount.getValue() + statusCount.getCount());
                    }
                }
            }
            this.value = statusCounts.stream().map(statusCount -> statusCount.getCount()).reduce(0L, Long::sum);
        }
    }

    @Getter
    @Setter
    private class FieldValueCount {
        private Integer name;
        private Long value;
        private String type;
        private String label;
        public FieldValueCount(Integer name, Long value) {
            this.name = Objects.isNull(name) ? -2 : name;
            this.value = value;
            this.type = "analysisPriority";
            this.label = Objects.isNull(name) ? "" : AnalysisPriorityValues.getLabelByCode(name);
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChangeRequestStatusCountByFieldValue {
        private Integer status;
        private Integer typeCount;
        private String type;
        private Long count;
        private String statusLabel;
    }
}
