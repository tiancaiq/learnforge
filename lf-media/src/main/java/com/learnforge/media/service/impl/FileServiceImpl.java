package com.learnforge.media.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.common.exceptions.DbException;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.media.config.PlatformProperties;
import com.learnforge.media.domain.dto.FileDTO;
import com.learnforge.media.domain.po.File;
import com.learnforge.media.enums.FileErrorInfo;
import com.learnforge.media.enums.FileStatus;
import com.learnforge.media.mapper.FileMapper;
import com.learnforge.media.service.IFileService;
import com.learnforge.media.storage.IFileStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * File table, can be a regular file, image, etc. Service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

    private final IFileStorage fileStorage;
    private final PlatformProperties properties;

    @Override
    public FileDTO uploadFile(MultipartFile file) {
        // 1. Get file name
        String originalFilename = file.getOriginalFilename();
        // 2. Generate new file name
        String filename = generateNewFileName(originalFilename);
        // 3. Get file stream
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new CommonException("File read exception", e);
        }
        // 4. Upload file
        String requestId = fileStorage.uploadFile(filename, inputStream, file.getSize());
        // 5. Write to database
        File fileInfo = null;
        try {
            fileInfo = new File();
            fileInfo.setFilename(originalFilename);
            fileInfo.setKey(filename);
            fileInfo.setStatus(FileStatus.UPLOADED);
            fileInfo.setRequestId(requestId);
            fileInfo.setPlatform(properties.getFile());
            save(fileInfo);
        } catch (Exception e) {
            log.error("File information save exception", e);
            fileStorage.deleteFile(filename);
            throw new DbException(FileErrorInfo.Msg.FILE_UPLOAD_ERROR);
        }
        // 6. Return
        FileDTO fileDTO = new FileDTO();
        fileDTO.setId(fileInfo.getId());
        fileDTO.setPath(fileInfo.getPlatform().getPath() + filename);
        fileDTO.setFilename(originalFilename);
        return fileDTO;
    }

    @Override
    public FileDTO getFileInfo(Long id) {
        File file = getById(id);
        if (file == null) {
            return null;
        }
        return FileDTO.of(file.getId(), file.getFilename(), file.getPlatform().getPath() + file.getKey());
    }

    private String generateNewFileName(String originalFilename) {
        // 1. Get suffix
        String suffix = StringUtils.subAfter(originalFilename, ".", true);
        // 2. Generate new file name
        return UUID.randomUUID().toString(true) + "." + suffix;
    }
}
