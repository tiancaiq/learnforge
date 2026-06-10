package com.learnforge.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learnforge.api.cache.RoleCache;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.enums.UserType;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.user.domain.po.UserDetail;
import com.learnforge.user.domain.query.UserPageQuery;
import com.learnforge.user.domain.vo.StaffVO;
import com.learnforge.user.service.IStaffService;
import com.learnforge.user.service.IUserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Employee Details Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-12
 */
@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements IStaffService {

    private final IUserDetailService detailService;
    private final RoleCache roleCache;
    @Override
    public PageDTO<StaffVO> queryStaffPage(UserPageQuery query) {
        // 1. Search
        Page<UserDetail> p = detailService.queryUserDetailByPage(query, UserType.STAFF);
        // 2. Process VO
        return PageDTO.of(p, u -> {
            StaffVO v = BeanUtils.toBean(u, StaffVO.class);
            v.setRoleName(roleCache.getRoleName(u.getRoleId()));
            return v;
        });
    }
}
