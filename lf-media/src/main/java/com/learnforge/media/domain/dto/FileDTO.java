package com.learnforge.media.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(description = "File information entity")
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {
    @ApiModelProperty(value = "File id", example = "1")
    private Long id;
    @ApiModelProperty(value = "File name", example = "Image.jpg")
    private String filename;
    @ApiModelProperty(value = "File access path", example = "a.jpg")
    private String path;

    public static FileDTO of(Long id, String filename, String path){
        return new FileDTO(id, filename, path);
    }
}
