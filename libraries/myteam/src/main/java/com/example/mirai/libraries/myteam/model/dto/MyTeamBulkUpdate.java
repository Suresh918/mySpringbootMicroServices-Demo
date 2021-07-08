package com.example.mirai.libraries.myteam.model.dto;

import com.example.mirai.libraries.core.model.User;
import lombok.Getter;
import lombok.Setter;
import net.bytebuddy.utility.RandomString;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class MyTeamBulkUpdate {
    private List<Long> caseObjectIds;
    private List<String> caseObjectNumbers;
    private String criteria;
    private String viewCriteria;
    private String role;
    private User userToAdd;
    private User userToRemove;
    private String id;
    public String getGeneratedKey() {
        if (Objects.isNull(this.id))
            this.id = RandomString.make();
        return this.id;
    }
}
