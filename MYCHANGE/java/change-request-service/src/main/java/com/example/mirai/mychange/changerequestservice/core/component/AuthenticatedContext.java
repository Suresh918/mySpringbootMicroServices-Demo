package com.example.mirai.projectname.changerequestservice.core.component;

import com.example.mirai.libraries.audit.AuditableUserExtractorInterface;
import com.example.mirai.libraries.core.model.User;
import com.example.mirai.libraries.entity.ApplicationEventActorExtractorInterface;
import com.example.mirai.libraries.event.EventActorExtractorInterface;
import com.example.mirai.libraries.security.abac.model.SubjectElement;
import com.example.mirai.libraries.security.core.RBACInitializerInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthenticatedContext implements RBACInitializerInterface, AuditableUserExtractorInterface, ApplicationEventActorExtractorInterface, EventActorExtractorInterface {

    @Value("${mirai.projectname.changerequestservice.system-account.user-id}")
    private String systemAccountUserId;

    @Override
    public String getPrincipal() {
        return getUserId();
    }

    @Override
    public Set<SubjectElement> getSubjects() {
        Set<SubjectElement> subjectElements = new HashSet<>();
        String principal = getPrincipal();
        Set<String> roles = getRoles();
        SubjectElement subjectElement = new SubjectElement(principal, roles);
        subjectElements.add(subjectElement);
        return subjectElements;
    }

    @Override
    public Set<String> getRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority().substring(5)).collect(Collectors.toSet());
    }

    public Set getGroupMembership() {
        Jwt jwt = getJwt();
        if (jwt == null)
            return null;

        List<String> groupMemberships = jwt.getClaimAsStringList("group_membership");

        if (groupMemberships == null)
            return null;
        return groupMemberships.stream().map(groupMembership -> groupMembership.substring(5)).collect(Collectors.toSet());
    }

    public String getUserId() {
        return (String) getClaim("user_id");
    }

    public String getFullName() {
        return (String) getClaim("full_name");
    }

    public String getEmail() {
        return (String) getClaim("email");
    }

    public String getDepartmentNumber() {
        return (String) getClaim("department_number");
    }

    public String getDepartmentName() {
        return (String) getClaim("department_name");
    }

    public String getAbbreviation() {
        return (String) getClaim("abbreviation");
    }

    public String getEmployeeNumber() {
        return (String) getClaim("employee_number");
    }

    private Object getClaim(String claim) {
        Jwt jwt = getJwt();
        if (jwt == null)
            return null;
        List<String> claims = jwt.getClaimAsStringList(claim);
        if (claims == null)
            return null;
        return claims.get(0);
    }


    private User getPrincipalUser() {
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null ||
                SecurityContextHolder.getContext().getAuthentication().getCredentials() == null)
            return null;
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Jwt getJwt() {
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null ||
                SecurityContextHolder.getContext().getAuthentication().getCredentials() == null)
            return null;
        return (Jwt) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }

    private User getUser() {
        User principalUser = getPrincipalUser();
        if (Objects.isNull(principalUser)) {
            return null;
        }
        User user = new User();
        user.setUserId(principalUser.getUserId());
        user.setFullName(principalUser.getFullName());
        user.setAbbreviation(principalUser.getAbbreviation());
        user.setEmail(principalUser.getEmail());
        user.setDepartmentName(principalUser.getDepartmentName());
        return user;
    }

    @Override
    public User getAuditableUser() {
        User auditor = getUser();
        if (Objects.isNull(auditor)) {
            User user = new User();
            user.setUserId(systemAccountUserId);
            return user;
        }
        return auditor;
    }

    @Override
    public User getApplicationEventActor() {
        return getUser();
    }

    @Override
    public User getEventActor() {
        User actor = getUser();
        if (Objects.isNull(actor)) {
            User user = new User();
            user.setUserId(systemAccountUserId);
            return user;
        }
        return actor;
    }
}
