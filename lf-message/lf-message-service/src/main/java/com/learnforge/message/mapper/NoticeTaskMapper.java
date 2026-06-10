package com.learnforge.message.mapper;

import com.learnforge.message.domain.po.NoticeTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * System announcement task table, which can be delayed or sent periodically Mapper interface
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-20
 */
public interface NoticeTaskMapper extends BaseMapper<NoticeTask> {

    @Select("SELECT user_id FROM notice_task_target WHERE task_id = #{task_id}")
    List<Long> queryTaskTargetByTaskId(Long taskId);
}
