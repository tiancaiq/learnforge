package com.learnforge.data.controller;

import com.learnforge.data.model.dto.BoardDataSetDTO;
import com.learnforge.data.model.vo.EchartsVO;
import com.learnforge.data.service.BoardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName BoardController
 * @Author wusongsong
 * @Date 2022/10/10 11:27
 * @Version
 **/
@RestController
@RequestMapping("/data/board")
@Api(tags = "Dashboard Data Operations")
@Slf4j
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("")
    @ApiOperation("Dashboard Data Retrieval")
    public EchartsVO boardData(@RequestParam("types") List<Integer> types) {
        return boardService.boardData(types);
    }

    @PutMapping("set")
    @ApiOperation("Dashboard Data Setup")
    public void setBoardData(@Validated @RequestBody BoardDataSetDTO boardDataSetDTO) {
        boardService.setBoardData(boardDataSetDTO);
    }
}
