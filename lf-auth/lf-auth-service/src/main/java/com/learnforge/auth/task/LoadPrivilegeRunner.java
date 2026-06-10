package com.learnforge.auth.task;

import cn.hutool.core.collection.CollectionUtil;
import com.learnforge.auth.common.domain.PrivilegeRoleDTO;
import com.learnforge.auth.service.IPrivilegeService;
import com.learnforge.auth.util.PrivilegeCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class LoadPrivilegeRunner{

    private final IPrivilegeService privilegeService;
    private final PrivilegeCache privilegeCache;

    @PostConstruct
    public void loadPrivilegeCache(){
        try {
            log.trace("Start Updating Permission Cache Data");
            // 1. Query Data
            List<PrivilegeRoleDTO> privilegeRoleDTOS = privilegeService.listPrivilegeRoles();
            if (CollectionUtil.isEmpty(privilegeRoleDTOS)) {
                return;
            }
            // 2. Cache
            privilegeCache.initPrivilegesCache(privilegeRoleDTOS);
            log.trace("Update Permission Cache Data Successfully!");
        }catch (Exception e){
            log.error("Update Permission Cache Data Failed! Reason: {}", e.getMessage());
        }
    }
}
