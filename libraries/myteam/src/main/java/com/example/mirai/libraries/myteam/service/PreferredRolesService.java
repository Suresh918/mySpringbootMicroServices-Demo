package com.example.mirai.libraries.myteam.service;

import com.example.mirai.projectname.libraries.user.model.PreferredRole;
import com.example.mirai.projectname.libraries.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class PreferredRolesService {
    private final UserService userService;

    public PreferredRolesService(UserService userService) {
        this.userService = userService;
    }

    public PreferredRole getPreferredRolesByUserId(String userId) {
        List userIds = new ArrayList(Arrays.asList(userId));
        List<PreferredRole> preferredRoles = userService.getPreferredRolesByUserIds(userIds);
        return Objects.nonNull(preferredRoles) && !preferredRoles.isEmpty() ? preferredRoles.get(0) : null;
    }

    public List<PreferredRole> getPreferredRolesByUserIds(List<String> userIds) {
        return userService.getPreferredRolesByUserIds(userIds);
    }
}
