package com.example.mirai.projectname.changerequestservice.scope.model.dto;

import com.example.mirai.projectname.changerequestservice.customerimpact.model.CustomerImpact;
import com.example.mirai.projectname.changerequestservice.scope.model.Scope;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScopeCustomerImpact {
    private Scope scope;
    private CustomerImpact customerImpact;
}
