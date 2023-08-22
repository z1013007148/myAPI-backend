package com.api.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.api.common.model.entity.UserInterfaceInfo;


public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * 返回剩下次数，null为出错
     */
    Integer invokeCount(long interfaceInfoId, long userId);

    /**
     * 返回用户对该接口的剩余调用次数
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    int getLeftNum(long interfaceInfoId, long userId);

    /**
     * 回滚接口调用次数
     * @param interfaceInfoId
     * @param userId
     * @param num
     * @return
     */
    public boolean rollBackCount(long interfaceInfoId, long userId, int num);
}
