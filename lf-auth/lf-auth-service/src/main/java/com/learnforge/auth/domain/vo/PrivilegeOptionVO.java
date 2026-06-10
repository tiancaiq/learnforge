package com.learnforge.auth.domain.vo;

import com.learnforge.auth.domain.po.Privilege;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "API privilege option entity")
public class PrivilegeOptionVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Privilege id", example = "1")
    private Long id;
    @ApiModelProperty(value = "Privilege description", example = "Add employee")
    private String intro;
    @ApiModelProperty(value = "Is selected", example = "true")
    private Boolean checked;

    public PrivilegeOptionVO() {
    }

    public PrivilegeOptionVO(Privilege privilege) {
        this.id = privilege.getId();
        this.intro = privilege.getIntro();
    }
}
