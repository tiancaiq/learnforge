package com.learnforge.data.controller;

import com.learnforge.data.model.dto.Top10DataSetDTO;
import com.learnforge.data.model.vo.Top10DataVO;
import com.learnforge.data.service.Top10Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName Top10Controller
 * @Author wusongsong
 * @Date 2022/10/10 19:27
 * @Version
 **/
@RestController
@Api(tags = "Workbench Top 10 Data Related Interfaces")
@RequestMapping("/data/top10")
public class Top10Controller {

    @Autowired
    private Top10Service top10Service;

    @GetMapping("")
    @ApiOperation("Top 10 Data Retrieval")
    public Top10DataVO getTop10Data() {
        return top10Service.getTop10Data();
    }

    @PutMapping("set")
    @ApiOperation("Set Top 10 Data")
    public void setTop10Data(@RequestBody @Validated Top10DataSetDTO top10DataSetDTO) {
        top10Service.setTop10Data(top10DataSetDTO);
    }

}
