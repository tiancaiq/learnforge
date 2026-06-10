package com.learnforge.media.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.client.course.CourseClient;
import com.learnforge.api.client.learning.LearningClient;
import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.course.MediaQuoteDTO;
import com.learnforge.api.dto.course.SectionInfoDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.exceptions.ForbiddenException;
import com.learnforge.common.utils.*;
import com.learnforge.media.constants.FileErrorInfo;
import com.learnforge.media.domain.dto.MediaDTO;
import com.learnforge.media.domain.dto.MediaUploadResultDTO;
import com.learnforge.media.domain.po.Media;
import com.learnforge.media.domain.query.MediaQuery;
import com.learnforge.media.domain.vo.MediaVO;
import com.learnforge.media.domain.vo.VideoPlayVO;
import com.learnforge.media.enums.FileStatus;
import com.learnforge.media.mapper.MediaMapper;
import com.learnforge.media.service.IMediaService;
import com.learnforge.media.storage.IMediaStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.learnforge.media.constants.FileErrorInfo.MEDIA_NOT_EXISTS;

/**
 * <p>
 * Media table, mainly video files. Service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-30
 */
@Service
@RequiredArgsConstructor
public class MediaServiceImpl extends ServiceImpl<MediaMapper, Media> implements IMediaService {

    private final IMediaStorage mediaStorage;

    private final CourseClient courseClient;

    private final LearningClient learningClient;

    private final UserClient userClient;

    @Override
    public String getUploadSignature() {
        return mediaStorage.getUploadSignature();
    }

    @Override
    public VideoPlayVO getPlaySignatureBySectionId(Long sectionId) {
        // 1. Query media course information according to sectionId
        SectionInfoDTO sectionInfo = courseClient.sectionInfo(sectionId);
        Long courseId = sectionInfo.getCourseId();
        // 2. Query user course table, whether it is a purchased course
        Long lessonId = learningClient.isLessonValid(courseId);

        if(lessonId != null){
            // 2.1. Yes, query media information, directly get signature
            Media media = getById(sectionInfo.getMediaId());
            AssertUtils.isNotNull(media, MEDIA_NOT_EXISTS);
            // 1) Get signature
            String signature =  mediaStorage.getPlaySignature(media.getFileId(), UserContext.getUser(), null);
            // 2) Return
            VideoPlayVO vo = new VideoPlayVO();
            vo.setSignature(signature);
            vo.setFileId(media.getFileId());
            return vo;
        }
        // 2.2. No, judge whether the course chapter is free
        Boolean trailer = sectionInfo.getTrailer();
        if(BooleanUtils.isFalse(trailer)) {
            // 2.3. Not free, throw exception
            throw new ForbiddenException(FileErrorInfo.MEDIA_NOT_FREE);
        }

        // 3. Free, get course information
        Media media = getById(sectionInfo.getMediaId());
        AssertUtils.isNotNull(media, MEDIA_NOT_EXISTS);
        // 4. Get signature
        String signature =  mediaStorage.getPlaySignature(
                media.getFileId(), UserContext.getUser(), sectionInfo.getFreeDuration());
        // 5. Return
        VideoPlayVO vo = new VideoPlayVO();
        vo.setSignature(signature);
        vo.setFileId(media.getFileId());
        return vo;
    }


    @Override
    public VideoPlayVO getPlaySignatureByMediaId(Long mediaId) {
        // 1. Query media information according to id
        Media media = getById(mediaId);
        // 2. Get signature
        String signature =  mediaStorage.getPlaySignature(media.getFileId(), UserContext.getUser(), null);
        // 3. Return
        VideoPlayVO vo = new VideoPlayVO();
        vo.setSignature(signature);
        vo.setFileId(media.getFileId());
        return vo;
    }

    @Override
    public PageDTO<MediaVO> queryMediaPage(MediaQuery query) {
        // 1. Pagination conditions
        Page<Media> mediaPage = new Page<>(query.getPageNo(), query.getPageSize());
        if(StringUtils.isNotBlank(query.getSortBy())){
            mediaPage.addOrder(new OrderItem(query.getSortBy(), query.getIsAsc()));
        }
        // 2. Pagination search
        lambdaQuery()
                .like(StringUtils.isNotBlank(query.getName()), Media::getFilename, query.getName())
                .page(mediaPage);
        // 3. Parse data
        List<Media> records = mediaPage.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(mediaPage);
        }
        List<Long> ids = new ArrayList<>(records.size());
        Set<Long> createIds = new HashSet<>();
        for (Media m : records) {
            ids.add(m.getId());
            createIds.add(m.getCreater());
        }
        createIds.remove(0L);
        // 4. Query reference count
        List<MediaQuoteDTO> mediaQuoteDTOS = courseClient.mediaUserInfo(ids);
        AssertUtils.isNotEmpty(mediaQuoteDTOS, FileErrorInfo.MEDIA_QUOTE_NOT_EXISTS);
        Map<Long, Integer> quoteMap = mediaQuoteDTOS
                .stream()
                .collect(Collectors.toMap(MediaQuoteDTO::getMediaId, MediaQuoteDTO::getQuoteNum));

        // 5. Query creator information
        Map<Long, String> userMap = null;
        if(CollUtils.isNotEmpty(createIds)) {
            List<UserDTO> users = userClient.queryUserByIds(createIds);
            AssertUtils.isNotEmpty(users, FileErrorInfo.USER_NOT_EXISTS);
            userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));
        }
        // 6. Data conversion
        List<MediaVO> list = new ArrayList<>(records.size());
        for (Media m : records) {
            MediaVO v = BeanUtils.toBean(m, MediaVO.class);
            v.setUseTimes(quoteMap.get(m.getId()));
            if(userMap != null) {
                v.setCreater(userMap.get(m.getCreater()));
            }
            list.add(v);
        }
        return new PageDTO<>(mediaPage.getTotal(), mediaPage.getPages(), list);
    }

    @Override
    public MediaDTO save(MediaUploadResultDTO result) {
        // 1. Query video information
        List<Media> list = mediaStorage.queryMediaInfos(result.getFileId());
        AssertUtils.isNotEmpty(list, MEDIA_NOT_EXISTS);
        // 2. Check existence, idempotent handling
        Media media = lambdaQuery().eq(Media::getFileId, result.getFileId()).one();
        if (media != null) {
            // Already exists and has been processed
            return BeanUtils.toBean(media, MediaDTO.class);
        }
        // 3. Query video information
        media = list.get(0);
        // 4. Directly save to database
        save(list.get(0));
        return BeanUtils.toBean(media, MediaDTO.class);
    }

    @Override
    public void updateMediaProcedureResult(Media media) {
        // 1. Check if fileId exists
        Media old = lambdaQuery().eq(Media::getFileId, media.getFileId()).one();
        if (old == null) {
            // 2. If not exists, add new
            save(media);
        }else {
            // 3. If exists, update
            lambdaUpdate()
                    .set(Media::getStatus, FileStatus.PROCESSED.getValue())
                    .set(Media::getCoverUrl, media.getCoverUrl())
                    .eq(Media::getId, old.getId())
                    .update();
        }
    }

    @Override
    @Transactional
    public void deleteMedia(String fileId) {
        // 1. Delete cloud file
        mediaStorage.deleteFile(fileId);
        // 2. Delete local information
        remove(new LambdaQueryWrapper<Media>().eq(Media::getFileId, fileId));
    }
}
