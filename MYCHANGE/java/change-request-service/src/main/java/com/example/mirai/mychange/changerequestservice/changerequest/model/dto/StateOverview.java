package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.libraries.entity.model.StatusOverview;
import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequestState;
import com.example.mirai.libraries.entity.model.StatusOverview.StatusCount;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StateOverview {

    private List<ChangeRequestStateCount> stateCounts = new ArrayList();

    public StateOverview(StatusOverview statusOverview) {
        Arrays.stream(ChangeRequestState.values()).forEach(state -> {
            stateCounts.add(new ChangeRequestStateCount(state, statusOverview.getStatusCounts()));
        });
    }

    @Getter
    @Setter
    private class ChangeRequestStateCount {
        private String state;
        private String stateLabel;
        private Long count;
        private List<StatusCount> statusCounts;

        private ChangeRequestStateCount(ChangeRequestState changeRequestState, List<StatusCount> statusCountsData) {
            this.state = changeRequestState.name();
            this.stateLabel = changeRequestState.getStateLabel();
            List<StatusCount> statusCounts = statusCountsData.stream()
                    .filter(statusCount -> Arrays.asList(changeRequestState.getStatuses()).contains(statusCount.getStatus())
            ).collect(Collectors.toList());
            this.statusCounts = statusCounts;
            this.count = statusCounts.stream().map(statusCount -> statusCount.getCount()).reduce(0L, Long::sum);
        }
    }
}
