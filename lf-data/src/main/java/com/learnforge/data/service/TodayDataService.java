package com.learnforge.data.service;


import com.learnforge.data.model.dto.TodayDataDTO;
import com.learnforge.data.model.vo.TodayDataVO;

/**
 * @author wusongsong
 * @since 2022/10/13 9:27
 **/
public interface TodayDataService {

    /**
     * Retrieve Today's Data
     * @return
     */
    TodayDataVO get();

    /**
     * Set today's data
     * @param todayDataDTO
     */
    void set(TodayDataDTO todayDataDTO);
}