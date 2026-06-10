package com.learnforge.search.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.search.domain.po.Interests;
import com.learnforge.api.dto.course.CategoryBasicDTO;

import java.util.List;

/**
 * <p>
 * User interest table, save interested secondary category id service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-21
 */
public interface IInterestsService extends IService<Interests> {

    void saveInterests(List<Long> interestedIds);

    List<CategoryBasicDTO> queryMyInterests();

    List<Long> queryMyInterestsIds();
}
