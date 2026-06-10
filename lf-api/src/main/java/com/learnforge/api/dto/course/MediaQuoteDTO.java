package com.learnforge.api.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName MediaQuoteDTO
 * @author wusongsong
 * @since 2022/7/18 17:43
 * @version 1.0.0
 **/
@ApiModel("Media Asset Reference Status")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MediaQuoteDTO {
    @ApiModelProperty("Media asset id")
    private Long mediaId;
    @ApiModelProperty("Reference Count")
    private Integer quoteNum;
}
