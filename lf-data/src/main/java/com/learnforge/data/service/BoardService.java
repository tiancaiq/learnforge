package com.learnforge.data.service;


import com.learnforge.data.model.dto.BoardDataSetDTO;
import com.learnforge.data.model.vo.EchartsVO;

import java.util.List;

/**
 * @ClassName BoardService
 * @Author wusongsong
 * @Date 2022/10/10 16:30
 * @Version
 **/
public interface BoardService {

    /**
     * Dashboard Data Retrieval
     *
     * @param types Data Type
     * @return
     */
    EchartsVO boardData(List<Integer> types);

    /**
     * Set Dashboard Data
     *
     * @param boardDataSetDTO
     */
    void setBoardData(BoardDataSetDTO boardDataSetDTO);
}