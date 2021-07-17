package com.example.mirai.projectname.reviewservice.reviewtask.model.dto;


import com.example.mirai.libraries.core.model.BaseView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ReviewTaskSummaries {
    Integer reviewTaskCount;
    Integer completedReviewTaskCount;
    List<BaseView> reviewTaskSummaries;
}
