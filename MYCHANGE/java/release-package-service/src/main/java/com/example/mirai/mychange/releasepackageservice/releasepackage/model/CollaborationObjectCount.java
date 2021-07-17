package com.example.mirai.projectname.releasepackageservice.releasepackage.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollaborationObjectCount {
    private Integer commentsCount;
    private Integer allActionsCount;
    private Integer openActionsCount;
    private Integer documentsCount;
    private Integer allAttachmentsCount;

    public CollaborationObjectCount() {
        this.commentsCount = 0;
        this.allActionsCount = 0;
        this.openActionsCount = 0;
        this.documentsCount = 0;
        this.allAttachmentsCount = 0;
    }
}
