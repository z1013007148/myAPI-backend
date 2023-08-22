package com.api.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInterfaceRollBackInfoMessage implements Serializable {
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 调用接口id
     */
    private Long interfaceInfoId;

    /**
     * 回滚调用次数，默认1次
     */
    private int num=1;
}
