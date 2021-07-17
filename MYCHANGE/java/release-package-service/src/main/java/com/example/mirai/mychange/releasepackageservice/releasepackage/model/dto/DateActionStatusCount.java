package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;


import java.util.Optional;

import com.example.mirai.libraries.entity.model.StatusOverview;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DateActionStatusCount {
    private Integer status;
    private String statusLabel;
    private Long count;
    private Long plannedEffectiveDatePastCount;
    private Long plannedReleaseDatePastCount;
    private Long plannedEffectiveDateSoonCount;
    private Long plannedReleaseDateSoonCount;
    private Long actionCount;

    public DateActionStatusCount(StatusOverview.StatusCount item, Optional<DateActionCount> dateActionCount) {
        this.count = item.getCount();
        this.status = item.getStatus();
        this.statusLabel = item.getStatusLabel();
        this.plannedEffectiveDatePastCount = dateActionCount.isEmpty() ? 0L : dateActionCount.get().getPlannedEffectiveDatePastCount();
        this.plannedReleaseDatePastCount = dateActionCount.isEmpty() ? 0L : dateActionCount.get().getPlannedReleaseDatePastCount();
        this.plannedEffectiveDateSoonCount = dateActionCount.isEmpty() ? 0L : dateActionCount.get().getPlannedEffectiveDateSoonCount();
        this.plannedReleaseDateSoonCount = dateActionCount.isEmpty() ? 0L : dateActionCount.get().getPlannedReleaseDateSoonCount();
        this.actionCount = dateActionCount.isEmpty() ? 0L : dateActionCount.get().getActionCount();
    }
}
