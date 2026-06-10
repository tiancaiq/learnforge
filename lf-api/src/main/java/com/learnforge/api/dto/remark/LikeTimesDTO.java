package com.learnforge.api.dto.remark;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class LikeTimesDTO {

    private Long bizId;
    private Integer likeTimes;
}
