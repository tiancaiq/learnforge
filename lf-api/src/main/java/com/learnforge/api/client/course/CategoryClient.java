package com.learnforge.api.client.course;

import com.learnforge.api.dto.course.CategoryBasicDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(contextId = "category",value = "course-service",path = "categorys")
public interface CategoryClient {

    /**
     * Get all courses and course categories
     * @return All courses and course categories
     */
    @GetMapping("getAllOfOneLevel")
    List<CategoryBasicDTO> getAllOfOneLevel();
}
