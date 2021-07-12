package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChangeRequestCategory {
    private String categoryId;
    private String description;
    private List<ChangeRequestStateCount> subCategories;

    @Getter
    @Setter
    public static class ChangeRequestStateCount {
        private String name;
        private String label;
        private Long count;
        private String type;
        private List<ChangeRequestBrief> items;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ChangeRequestBrief {
        private Long id;
        private String title;
    }
}
