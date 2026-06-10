package com.learnforge.media.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.media.domain.dto.MediaDTO;
import com.learnforge.media.domain.dto.MediaUploadResultDTO;
import com.learnforge.media.domain.po.Media;
import com.learnforge.media.domain.query.MediaQuery;
import com.learnforge.media.domain.vo.MediaVO;
import com.learnforge.media.domain.vo.VideoPlayVO;

/**
 * <p>
 * Media table, mainly video files. Service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-30
 */
public interface IMediaService extends IService<Media> {

    String getUploadSignature();

    VideoPlayVO getPlaySignatureBySectionId(Long fileId);

    MediaDTO save(MediaUploadResultDTO mediaResult);

    void updateMediaProcedureResult(Media media);

    void deleteMedia(String fileId);

    VideoPlayVO getPlaySignatureByMediaId(Long mediaId);

    PageDTO<MediaVO> queryMediaPage(MediaQuery query);
}
