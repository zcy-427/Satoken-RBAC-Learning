package io.github.zcy427.rbac.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

// 通用分页响应对象
@Data
@AllArgsConstructor
public class PageResult<T> {

    private List<T> list;
    private long page;
    private long size;
    private long total;
    private long pages;
}