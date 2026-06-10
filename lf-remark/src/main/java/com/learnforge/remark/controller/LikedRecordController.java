package com.learnforge.remark.controller;


import com.learnforge.remark.domain.dto.LikeRecordFormDTO;
import com.learnforge.remark.domain.po.LikedRecord;
import com.learnforge.remark.mapper.LikedRecordMapper;
import com.learnforge.remark.service.ILikedRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * Like Record Table Frontend Controller
 * </p>
 *
 * @author luke
 * @since 2026-05-26
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/likes")
@Api(tags = "likes ")
public class LikedRecordController {

    private final ILikedRecordService likedRecordService;

    @PostMapping
    @ApiOperation("like or cancel")
    public void addLikedRecord(@Valid @RequestBody LikeRecordFormDTO recordDTO){
        likedRecordService.addLikeRecord(recordDTO);
    }


    @GetMapping("list")
    @ApiOperation("query specific id like or not")
    public Set<Long> isBizLiked(@RequestParam("bizIds") List<Long> bizIds){
        return likedRecordService.isBizLiked(bizIds);
    }

}
