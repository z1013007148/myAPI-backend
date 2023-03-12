package com.api.project.service.impl.inner;

import com.api.common.model.entity.UserInterfaceInfo;
import com.api.project.common.ErrorCode;
import com.api.project.exception.BusinessException;
import com.api.project.service.UserInterfaceInfoService;
import com.api.common.service.InnerUserInterfaceInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }


    @Override
    public int getLeftNum(long interfaceInfoId, long userId) {
        // 判断合法
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return userInterfaceInfoService.getLeftNum(interfaceInfoId, userId);
    }
}
