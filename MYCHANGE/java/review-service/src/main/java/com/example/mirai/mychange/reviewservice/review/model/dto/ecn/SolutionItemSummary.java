package com.example.mirai.projectname.reviewservice.review.model.dto.ecn;

/*import com.example.mirai.libraries.teamcenter.ecn.model.SolutionItem;
import com.example.mirai.libraries.teamcenter.ecn.model.Tpd;*/
import com.example.mirai.libraries.deltareport.model.dto.SolutionItem;
import com.example.mirai.libraries.deltareport.model.dto.Tpd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolutionItemSummary {
    private String id;
    private String name;
    private String title;
    private List<String> sourceSystem;
    private List<SolutionItemSummary> solutionItems;
    private Boolean isTpd=false;
    private String uid;
    private String completeTpdId;


    public SolutionItemSummary(Tpd tpd) {
        this.id = tpd.getId();
        this.name = tpd.getName();
        this.title = tpd.getDescription();
        this.completeTpdId = tpd.getCompleteTpdId();
        this.sourceSystem = new ArrayList<>(Arrays.asList("TEAMCENTER"));
        this.solutionItems = new ArrayList<>();
        this.isTpd = true;
        this.uid = tpd.getUid();
    }
    public SolutionItemSummary(SolutionItem solutionItem) {
        this.id = solutionItem.getSolutionItemId();
        this.name = solutionItem.getSolutionItemId();
        this.title = solutionItem.getDescription();
        this.sourceSystem = new ArrayList<>(Arrays.asList("TEAMCENTER"));
        this.solutionItems = new ArrayList<>();
        this.isTpd = false;
        this.uid = solutionItem.getId();
    }
}
