package com.example.mirai.projectname.changerequestservice.changerequest.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Embeddable
@Getter
@Setter
public class RuleSet implements Serializable {
    private String ruleSetName;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "rules", joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    @OrderColumn
    @Column(length = 2048)
    private List<String> rules;
    //initializer block
    /*{
        this.rules = new ArrayList<>();
    }*/
}
