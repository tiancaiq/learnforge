package com.learnforge.auth.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learnforge.auth.domain.dto.MenuDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Privilege table, including menu privilege and access path privilege
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("menu")
@NoArgsConstructor
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @TableId
    private Long id;

    /**
     * Parent menu id, default 0 means no parent menu
     */
    private Long parentId;

    /**
     * Has child menu, default false
     */
    private Boolean hasChildren;

    /**
     * Menu text
     */
    private String label;

    /**
     * Menu path
     */
    private String path;

    /**
     * Menu icon
     */
    private String icon;

    /**
     * Order priority, default 127
     */
    private Integer priority;

    /**
     * Creation Time
     */
    private LocalDateTime createTime;

    /**
     * Update Time
     */
    private LocalDateTime updateTime;

    /**
     * Creator id
     */

    private Long creater;

    /**
     * Updater id
     */

    private Long updater;

    /**
     * Department id
     */
    private Long depId;

    /**
     * Logical deletion, default 0
     */
    private Integer deleted;


    public Menu(MenuDTO dto) {
        this.id = dto.getId();
        this.parentId = dto.getParentId();
        this.label = dto.getLabel();
        this.path = dto.getPath();
        this.icon = dto.getIcon();
        this.priority = dto.getPriority();
    }

    public MenuDTO toDTO(){
        MenuDTO dto = new MenuDTO();
        dto.setId(id);
        dto.setPath(path);
        dto.setParentId(parentId);
        dto.setLabel(label);
        dto.setIcon(icon);
        dto.setPriority(priority);
        return dto;
    }
}
