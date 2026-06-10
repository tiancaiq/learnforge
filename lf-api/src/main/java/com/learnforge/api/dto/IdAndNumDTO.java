package com.learnforge.api.dto;

import com.learnforge.common.utils.CollUtils;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ID and Num Model, The Number of Items Corresponding to an ID Can Be Used to Query the Relationship Between ID and Num
 * @author wusongsong
 * @since 2022/8/3 9:27
 * @version 1.0.0
 **/
@Data
public class IdAndNumDTO {
    private Long id;
    private Integer num;

    public static Map<Long, Integer> toMap(List<IdAndNumDTO> list){
        if (CollUtils.isEmpty(list)) {
            return CollUtils.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(IdAndNumDTO::getId, IdAndNumDTO::getNum));
    }
}
