package com.learnforge.data.model.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @ClassName BoardDataSetDTO
 * @Author wusongsong
 * @Date 2022/10/10 19:14
 * @Version
 **/
@Data
public class BoardDataSetDTO {
    @NotNull(message = "Version")
    private Integer version;
    @NotNull(message = "Data Type Cannot Be Empty")
    @Min(value = 1, message = "Data Type 1-9")
    @Max(value = 9, message = "Data Type 1-9")
    private Integer type;
    @NotNull(message = "Set Data Cannot Be Empty")
    @Size(min = 15, max = 15, message = "Need to Set 15 Days of Data")
    private List<Double> data;
}
