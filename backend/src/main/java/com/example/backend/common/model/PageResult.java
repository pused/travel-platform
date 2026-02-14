package com.example.backend.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author caiya
 * @Description 通用分页返回结果
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {
    private T records;
    private long current;
    private long size;
    private long total;
}
