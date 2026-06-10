package com.learnforge.search.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.cache.CategoryCache;
import com.learnforge.api.dto.course.CategoryBasicDTO;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.search.domain.po.Interests;
import com.learnforge.search.mapper.InterestsMapper;
import com.learnforge.search.service.IInterestsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * User interest table, save interested secondary category id service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-21
 */
@Service
public class InterestsServiceImpl extends ServiceImpl<InterestsMapper, Interests> implements IInterestsService {

    @Autowired
    private CategoryCache categoryCache;

    @Override
    public void saveInterests(List<Long> interestedIds) {
        // 1. Get current user
        Long userId = UserContext.getUser();
        String ids = CollUtils.joinIgnoreNull(interestedIds, ",");
        if(StringUtils.isBlank(ids)){
            // No interest preferences, direct deletion
            removeById(userId);
            return;
        }
        // 2. Package data
        Interests interests = new Interests();
        interests.setId(userId);
        interests.setInterests(CollUtils.join(interestedIds, ","));
        // 3. Save
        saveOrUpdate(interests);
    }

    @Override
    public List<CategoryBasicDTO> queryMyInterests() {
        // 1. Get interest id
        List<Long> ids = queryMyInterestsIds();
        // 2. Get cache result
        return categoryCache.queryCategoryByIds(ids);
    }

    @Override
    public List<Long> queryMyInterestsIds() {
        // 1. Get current user
        Long userId = UserContext.getUser();
        // 2. Query interest
        Interests interests = getById(userId);
        if (interests == null || StringUtils.isBlank(interests.getInterests())) {
            return CollUtils.emptyList();
        }
        // 3. Get category information
        String[] ids = interests.getInterests().split(",");
        if (ids.length == 0) {
            return CollUtils.emptyList();
        }
        try {
            // 4. Convert and return
            return Arrays.stream(ids).map(Long::valueOf).collect(Collectors.toList());
        } catch (Exception e) {
            // 5. Data conversion exception, return empty
            return CollUtils.emptyList();
        }
    }
}
