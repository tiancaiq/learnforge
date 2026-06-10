package com.learnforge.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.course.domain.dto.CategoryAddDTO;
import com.learnforge.course.domain.dto.CategoryDisableOrEnableDTO;
import com.learnforge.course.domain.dto.CategoryListDTO;
import com.learnforge.course.domain.dto.CategoryUpdateDTO;
import com.learnforge.course.domain.po.Category;
import com.learnforge.course.domain.po.Course;
import com.learnforge.course.domain.vo.CategoryInfoVO;
import com.learnforge.course.domain.vo.CategoryVO;
import com.learnforge.course.domain.vo.SimpleCategoryVO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Course Category Service
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-14
 */
public interface ICategoryService extends IService<Category> {

    /**
     * Pagination Query Course Information
     *
     * @param categoryPageDTO Pagination Parameters
     * @return Course Pagination Information
     */
    List<CategoryVO> list(CategoryListDTO categoryPageDTO);

    /**
     * Add Course Pagination
     *
     * @param categoryAddDTO Category Information
     */
    void add(CategoryAddDTO categoryAddDTO);

    /**
     * Get course category information
     * @param id Course ID
     * @return Course Category Information
     */
    CategoryInfoVO get(Long id);

    /**
     * Delete Course Category
     * @param id Category ID
     */
    void delete(Long id);

    /**
     * Enable or Disable Course Category
     */
    void disableOrEnable(CategoryDisableOrEnableDTO categoryDisableOrEnableDTO);

    /**
     * Update Course Category Information
     */
    void update(CategoryUpdateDTO categoryUpdateDTO);

    /**
     * Get All Category Data and Structure
     */
    List<SimpleCategoryVO> all(Boolean admin);

    /**
     * Get Course Category ID and Name
     * @return Course Category ID and Name
     */
    Map<Long, String> getCateIdAndName();

    List<CategoryVO> allOfOneLevel();

    /**
     * Query Category List by Course Category ID
     * @param ids Course Category ID
     * @return Category List
     */
    List<Category> queryByIds(List<Long> ids);

    /**
     * Query Course Category Information by Level Three Course Category
     * @param thirdCateIdList Level Three Course Category
     * @return Course Category Information
     */
    Map<Long, String> queryByThirdCateIds(@RequestParam("thirdCateIdList") List<Long> thirdCateIdList);

    /**
     * Get course category information
     *
     * @param course
     * @return
     */
    List<String> queryCourseCategorys(Course course);

    /**
     * Validate Course Category Compliance and Return Level One to Three Category ID List in Order
     *
     * @param thirdCateId Level Three Course Category
     * @return Level One to Three Category ID List
     */
    List<Long> checkCategory(Long thirdCateId);
}
