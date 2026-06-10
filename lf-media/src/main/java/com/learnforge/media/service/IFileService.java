package com.learnforge.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.media.domain.dto.FileDTO;
import com.learnforge.media.domain.po.File;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * File table, can be a regular file, image, etc. Service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-30
 */
public interface IFileService extends IService<File> {

    FileDTO uploadFile(MultipartFile file);

    FileDTO getFileInfo(Long id);
}
