package com.learnforge.data.service;


import com.learnforge.data.model.dto.Top10DataSetDTO;
import com.learnforge.data.model.vo.Top10DataVO;

/**
 * @author wusongsong
 * @since 2022/10/10 19:39
 **/
public interface Top10Service {

    /**
     * Get top data
     *
     * @return
     */
    Top10DataVO getTop10Data();

    /**
     * Top 10 data setting
     * @param top10DataSetDTO
     */
    void setTop10Data(Top10DataSetDTO top10DataSetDTO);
}