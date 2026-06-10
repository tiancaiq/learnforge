package com.learnforge.data.controller;


import com.learnforge.data.model.dto.TodayDataDTO;
import com.learnforge.data.model.vo.TodayDataVO;
import com.learnforge.data.service.TodayDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Today's Data
 * @ClassName TodayDataController
 * @Author wusongsong
 * @Date 2022/10/13 9:21
 * @Version
 **/
@RestController
@RequestMapping("/data/today")
@Api(tags = "Today's Data Related Interfaces")
public class TodayDataController {

    @Autowired
    private TodayDataService todayDataService;

    @GetMapping("")
    @ApiOperation("Retrieve Today's Data")
    public TodayDataVO get(){
        return todayDataService.get();
    }

    @PutMapping("set")
    @ApiOperation("Set Online Data")
    public void set(@RequestBody TodayDataDTO todayDataDTO) {
        todayDataService.set(todayDataDTO);
    }
}
