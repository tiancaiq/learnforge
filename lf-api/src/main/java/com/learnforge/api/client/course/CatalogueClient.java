package com.learnforge.api.client.course;

import com.learnforge.api.dto.course.CataSimpleInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(contextId = "catalogue", value = "course-service",path = "catalogues")
public interface CatalogueClient {

    /**
     * Query directory information by directory ID list
     *
     * @param ids Directory ID list
     * @return Directory basic information corresponding to the ID list
     */
    @GetMapping("/batchQuery")
    List<CataSimpleInfoDTO> batchQueryCatalogue(@RequestParam("ids") Iterable<Long> ids);


}