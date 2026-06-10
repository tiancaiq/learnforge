package com.learnforge.auth.util;

import cn.hutool.json.JSONUtil;
import com.learnforge.auth.common.domain.PrivilegeRoleDTO;
import com.learnforge.auth.domain.po.Privilege;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.learnforge.auth.common.constants.JwtConstants.AUTH_PRIVILEGE_KEY;
import static com.learnforge.auth.common.constants.JwtConstants.AUTH_PRIVILEGE_VERSION_KEY;

@Slf4j
@Component
public class PrivilegeCache {
    private final BoundHashOperations<String, String, String> hashOps;
    private final StringRedisTemplate stringRedisTemplate;

    public PrivilegeCache(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.hashOps = stringRedisTemplate.boundHashOps(AUTH_PRIVILEGE_KEY);
    }

    public void initPrivilegesCache(List<PrivilegeRoleDTO> list) {
        // 1. Assemble permissions corresponding to roles
        Map<String, String> map = new HashMap<>();
        for (PrivilegeRoleDTO prDTO : list) {
            map.put(prDTO.getId().toString(), JSONUtil.toJsonStr(prDTO));
        }
        // 2. Write to redis
        hashOps.putAll(map);
        // 3. Increment version
        incrementVersion();
    }

    public void cacheSinglePrivilege(Privilege p, Set<Long> roleIds) {
        try {
            PrivilegeRoleDTO privilegeRoleDTO = new PrivilegeRoleDTO();
            privilegeRoleDTO.setId(p.getId());
            privilegeRoleDTO.setAntPath(p.getMethod() + ":" + p.getUri());
            privilegeRoleDTO.setRoles(roleIds);
            privilegeRoleDTO.setInternal(p.getInternal());
            hashOps.put(p.getId().toString(), JSONUtil.toJsonStr(privilegeRoleDTO));
            incrementVersion();
        } catch (Exception e) {
            log.error("Failed to cache permission information. ->", e);
            throw new RuntimeException(e);
        }
    }

    public void removePrivilegeCacheById(Long id) {
        hashOps.delete(id);
        incrementVersion();
    }

    public void removePrivilegeCacheByIds(List<Long> ids) {
        hashOps.delete(ids.toArray());
        incrementVersion();
    }


    private void incrementVersion() {
        stringRedisTemplate.opsForValue().increment(AUTH_PRIVILEGE_VERSION_KEY, 1);
    }

    public void removeCacheByRoleId(Long id) {
        // Query all permission information
        Map<String, String> cacheMap = hashOps.entries();
        if(CollUtils.isEmpty(cacheMap)){
            return;
        }
        // Record modified data
        Map<String, String> modified = new HashMap<>();
        for (Map.Entry<String, String> en : cacheMap.entrySet()) {
            // Get permission data
            String value = en.getValue();
            PrivilegeRoleDTO prDTO = JsonUtils.toBean(value, PrivilegeRoleDTO.class);
            // Attempt to remove role id
            boolean remove = prDTO.getRoles().remove(id);
            if(remove){
                modified.put(en.getKey(), JsonUtils.toJsonStr(prDTO));
            }
        }
        // Write back to cache
        hashOps.putAll(modified);
        incrementVersion();
    }
}
