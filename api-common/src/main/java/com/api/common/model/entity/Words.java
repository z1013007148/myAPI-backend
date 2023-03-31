package com.api.common.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Words implements Serializable {
    private Long id;
    private String content;
    private Long usedNum;
    private Date createTime;
}
