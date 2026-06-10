package com.learnforge.user.service;

import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.user.domain.query.UserPageQuery;
import com.learnforge.user.domain.vo.StaffVO;

/**
 * <p>
 * Employee Details Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-12
 */
public interface IStaffService {
    PageDTO<StaffVO> queryStaffPage(UserPageQuery pageQuery);
}
