package com.example.mirai.projectname.releasepackageservice.releasepackage.model.dto;


import java.util.List;

import com.example.mirai.libraries.core.model.BaseEntityList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReleasePackageReorderPrerequisites {
    private BaseEntityList<Overview> releasePackagePrerequisites;
    private List<String> warningMessages;
}
