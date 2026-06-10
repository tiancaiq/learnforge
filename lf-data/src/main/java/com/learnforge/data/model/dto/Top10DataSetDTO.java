package com.learnforge.data.model.dto;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @ClassName Top10DataSetDTO
 * @Author wusongsong
 * @Date 2022/10/10 19:57
 * @Version
 **/
@Data
@Validated
public class Top10DataSetDTO {
    private Integer version = 0;
    @Size(min = 10, message = "Data Must Be Set for at Least 10 Items")
    private List<Top10DataSetUnitDTO> data;

    @Data
    @Validated
    public static class Top10DataSetUnitDTO {
        @NotNull(message = "Category Name Cannot Be Empty")
        private String category;
        @NotNull(message = "Course Name Cannot Be Empty")
        private String name;
        @NotNull(message = "New Student Count Cannot Be Empty")
        private Integer newStuNum;
        @NotNull(message = "Order Amount Cannot Be Empty")
        private Double orderAmount;
    }
}
