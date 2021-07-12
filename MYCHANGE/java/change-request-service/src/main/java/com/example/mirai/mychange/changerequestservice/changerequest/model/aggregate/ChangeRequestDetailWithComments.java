package com.example.mirai.projectname.changerequestservice.changerequest.model.aggregate;

import com.example.mirai.projectname.changerequestservice.migration.model.ChangeRequestCommentMigrate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRequestDetailWithComments {
    private ChangeRequestDetail changeRequestDetail;
    private List<ChangeRequestCommentMigrate> comments;
}
