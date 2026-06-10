package com.learnforge.user.domain.query;

import com.learnforge.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Teacher paginated query conditions")
public class UserPageQuery extends PageQuery {
    @ApiModelProperty(value = "Account status")
    private Integer status;
    @ApiModelProperty(value = "TeacherName")
    private String name;
    @ApiModelProperty(value = "Phone number")
    private String phone;
}
