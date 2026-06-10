package com.learnforge.api.client.remark.fallback;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Sets;
import com.learnforge.api.client.learning.LearningClient;
import com.learnforge.api.client.remark.RemarkClient;
import com.learnforge.api.dto.leanring.LearningLessonDTO;
import com.learnforge.common.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Slf4j
public class RemarkClientFallback implements FallbackFactory<RemarkClient> {

    @Override
    public RemarkClient create(Throwable cause) {
        log.error("Query remark exception", cause);
        return new RemarkClient() {
            @Override
            public Set<Long>  isBizLiked(Iterable<Long> bizIds) {
                return CollUtils.emptySet();
            }

        };
    }
}
