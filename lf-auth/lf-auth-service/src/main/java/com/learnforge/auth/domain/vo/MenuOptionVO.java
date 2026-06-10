package com.learnforge.auth.domain.vo;

import com.learnforge.auth.domain.po.Menu;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "Menu option entity")
public class MenuOptionVO {
    @ApiModelProperty(value = "Menu id", example = "1")
    private Long id;

    @ApiModelProperty(value = "Parent menu id", example = "0")
    private Long parentId;

    @ApiModelProperty(value = "Menu text", example = "System management")
    private String label;

    @ApiModelProperty(value = "Menu icon", example = "el-icon-sys")
    private String icon;

    @ApiModelProperty(value = "Has child menu", example = "false")
    private Boolean hasChildren;

    @ApiModelProperty(value = "Menu order", example = "1")
    private Integer priority;

    @ApiModelProperty(value = "Child menu collection")
    private List<MenuOptionVO> subMenus;

    public MenuOptionVO() {
    }

    public MenuOptionVO(Menu menu) {
        this.id = menu.getId();
        this.parentId = menu.getParentId();
        this.label = menu.getLabel();
        this.icon = menu.getIcon();
        this.hasChildren = menu.getHasChildren();
        this.priority = menu.getPriority();
    }
}
