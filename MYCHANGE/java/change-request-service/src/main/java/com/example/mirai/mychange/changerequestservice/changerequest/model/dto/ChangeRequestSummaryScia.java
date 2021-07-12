package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import com.example.mirai.projectname.changerequestservice.changerequest.model.ChangeRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChangeRequestSummaryScia {
    private Long id;
    private String title;
    private String problemDescription;
    private String productId;
    private List<String> reasonsForChange;

    public ChangeRequestSummaryScia(ChangeRequest changeRequest) {
        this.id = changeRequest.getId();
        this.title = changeRequest.getTitle();
        this.problemDescription = changeRequest.getProblemDescription();
        this.productId = changeRequest.getProductId();
        this.reasonsForChange = new ArrayList<>();
        this.reasonsForChange.addAll(changeRequest.getReasonsForChange());
    }
}
