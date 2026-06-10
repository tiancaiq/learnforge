package com.learnforge.media.controller;


import com.learnforge.media.domain.dto.FileDTO;
import com.learnforge.media.service.IFileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * File table, can be a regular file, image, etc. Frontend controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-30
 */
@RestController
@RequestMapping("/files")
@Api(tags = "Media management related interfaces")
@RequiredArgsConstructor
public class FileController {

    private final IFileService fileService;

    @ApiOperation("Upload file")
    @PostMapping
    public FileDTO uploadFile(
            @ApiParam(value = "File data") @RequestParam("file")MultipartFile file){
        return fileService.uploadFile(file);
    }

    @ApiOperation("Get file information")
    @GetMapping("/{id}")
    public FileDTO getFileInfo(
            @ApiParam(value = "File id", example = "1") @PathVariable("id") Long id){
        return fileService.getFileInfo(id);
    }

    @ApiOperation("Delete file")
    @DeleteMapping("/{id}")
    public void deleteFileById(
            @ApiParam(value = "File id", example = "1") @PathVariable("id") Long id) {
        fileService.removeById(id);
    }
}
