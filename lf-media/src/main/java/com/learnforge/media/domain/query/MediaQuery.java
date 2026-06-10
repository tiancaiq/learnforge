package com.learnforge.media.domain.query;

import com.learnforge.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Media search conditions")
public class MediaQuery extends PageQuery {
    @ApiModelProperty("Media name keyword")
    private String name;
}
