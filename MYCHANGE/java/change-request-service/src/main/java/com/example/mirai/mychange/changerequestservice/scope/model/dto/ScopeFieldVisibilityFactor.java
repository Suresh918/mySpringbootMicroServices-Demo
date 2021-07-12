package com.example.mirai.projectname.changerequestservice.scope.model.dto;

import com.example.mirai.libraries.core.annotation.SecureCaseAction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScopeFieldVisibilityFactor {
    private Boolean showExistingPartQuestion;
    private Boolean showOtherQuestions;
}
