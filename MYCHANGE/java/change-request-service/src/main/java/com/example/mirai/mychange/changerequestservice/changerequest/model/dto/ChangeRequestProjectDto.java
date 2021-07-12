package com.example.mirai.projectname.changerequestservice.changerequest.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.ProjectDto;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRequestProjectDto {
    private ProjectDto projectDto;
    private Long id;
}
