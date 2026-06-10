package com.learnforge.api.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.learnforge.api.client.auth.AuthClient;
import com.learnforge.api.dto.auth.RoleDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.enums.UserType;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RoleCache {

    private final Cache<Long, RoleDTO> roleCaches;
    private final AuthClient authClient;

    public String getRoleName(Long roleId) {
        RoleDTO roleDTO = roleCaches.get(roleId, authClient::queryRoleById);
        if (roleDTO == null) {
            return null;
        }
        return roleDTO.getName();
    }

    public String exchangeRoleName(UserDTO u) {
        if (u == null) {
            return "--";
        }
        if (UserType.STUDENT.equalsValue(u.getType())) {
            // Student, directly return role name
            return u.getName();
        } else {
            // Administrator needs to concatenate role name
            return getRoleName(u.getRoleId()) + "-" + u.getName();
        }
    }
}
