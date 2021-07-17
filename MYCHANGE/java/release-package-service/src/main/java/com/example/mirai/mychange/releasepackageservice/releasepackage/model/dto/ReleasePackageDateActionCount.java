package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.example.mirai.libraries.core.annotation.JoinKey;
import com.example.mirai.libraries.core.annotation.ViewMapper;
import com.example.mirai.libraries.core.model.BaseView;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@NoArgsConstructor
@Getter
@Setter
public class ReleasePackageDateActionCount implements BaseView {
    @Id
    @JoinKey
    private Long id;
    private Integer status;
    private Long actionCount;
    private Long plannedEffectiveDateSoon;
    private Long plannedReleaseDateSoon;
    private Long plannedEffectiveDatePast;
    private Long plannedReleaseDatePast;


    @ViewMapper
    public ReleasePackageDateActionCount(Long id, Integer status, Long actionCount, Long plannedEffectiveDateSoon,
                                         Long plannedReleaseDateSoon, Long plannedEffectiveDatePast, Long plannedReleaseDatePast) {
        this.id = id;
        this.status = status;
        this.actionCount = actionCount;
        this.plannedEffectiveDateSoon = plannedEffectiveDateSoon;
        this.plannedReleaseDateSoon = plannedReleaseDateSoon;
        this.plannedEffectiveDatePast = plannedEffectiveDatePast;
        this.plannedReleaseDatePast = plannedReleaseDatePast;
    }
}
