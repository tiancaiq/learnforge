package com.learnforge.media.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.media.domain.dto.MediaDTO;
import com.learnforge.media.domain.dto.MediaUploadResultDTO;
import com.learnforge.media.domain.query.MediaQuery;
import com.learnforge.media.domain.vo.MediaVO;
import com.learnforge.media.domain.vo.VideoPlayVO;
import com.learnforge.media.service.IMediaService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * Media table, mainly video files. Frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-30
 */
@RestController
@RequestMapping("/medias")
@Api(tags = "Media management related interfaces")
@RequiredArgsConstructor
public class MediaController {

    private final IMediaService mediaService;

    @ApiOperation("Paginated search for uploaded media information")
    @GetMapping
    public PageDTO<MediaVO> queryMediaPage(MediaQuery query){
        return mediaService.queryMediaPage(query);
    }

    @ApiOperation("Save media information after uploading video")
    @PostMapping
    public MediaDTO saveMedia(@RequestBody MediaUploadResultDTO result) {
        return mediaService.save(result);
    }

    @ApiOperation("Get upload video authorization signature")
    @GetMapping("/signature/upload")
    public String getUploadSignature(){
        return mediaService.getUploadSignature();
    }

    @ApiOperation("Get play video authorization signature")
    @GetMapping("/signature/play")
    public VideoPlayVO getPlaySignature(
            @ApiParam(value = "Section ID", example = "1", required = true) @RequestParam("sectionId") Long sectionId){
        return mediaService.getPlaySignatureBySectionId(sectionId);
    }

    @ApiOperation("Management end get preview video authorization signature")
    @GetMapping("/signature/preview")
    public VideoPlayVO getPreviewSignature(
            @ApiParam(value = "Media asset id", example = "1", required = true) @RequestParam("mediaId") Long mediaId){
        return mediaService.getPlaySignatureByMediaId(mediaId);
    }

    @ApiOperation("Delete media video")
    @DeleteMapping("{mediaId}")
    public void deleteMedia(
            @ApiParam(value = "Media asset id", example = "1", required = true) @PathVariable("mediaId") Long mediaId){
        mediaService.removeById(mediaId);
    }

    @ApiOperation("Batch delete media video")
    @DeleteMapping
    public void deleteMedias(
            @ApiParam(value = "Media id collection, for example 1,2,3", required = true) @RequestParam("ids") List<Long> mediaIds){
        mediaService.removeByIds(mediaIds);
    }
}
