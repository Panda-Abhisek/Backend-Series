package com.panda.lp.payloads;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScrollResponse<T> {
    private List<T> items;
    private String scrollId;
    private Boolean hasNext;
    private Integer pageSize;
}
