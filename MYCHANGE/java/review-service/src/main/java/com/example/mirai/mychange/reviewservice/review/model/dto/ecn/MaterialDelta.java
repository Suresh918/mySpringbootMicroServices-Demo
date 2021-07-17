package com.example.mirai.projectname.reviewservice.review.model.dto.ecn;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDelta {
    private String id;
    private ReviewMaterialDelta sapMdgSolutionItem;
    private ReviewSolutionItemDelta teamcenterSolutionItem;
    private List<WarningMessage> warningMessages;
    private Integer defectCount;
    boolean showTeamcenterGroupWarning = false;
    boolean showSapBaseGroupWarning = false;
    boolean showSapEnrichmentGroupWarning = false;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WarningMessage {
        private String title;
        private List<String> message;
    }

}
