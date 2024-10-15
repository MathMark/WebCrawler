package com.seo.dto;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageInfo {
    @Min(0)
    private int pageNumber = 0;
    @Min(1)
    @Max(100)
    private int pageSize = 10;
}
