package com.learnforge.auth.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.auth.domain.po.AccountRole;
import com.learnforge.auth.mapper.AccountRoleMapper;
import com.learnforge.auth.service.IAccountRoleService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Account-Role Association Table Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
@Service
public class AccountRoleServiceImpl extends ServiceImpl<AccountRoleMapper, AccountRole> implements IAccountRoleService {

}
