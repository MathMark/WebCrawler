package com.experimental.webcrawler.controller.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PagingForm {
    private int pageNumber = 0;
    private int pageSize = 10;
}
