package com.api.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.api.project.common.ErrorCode;
import com.api.project.exception.BusinessException;
import com.api.project.mapper.UserInterfaceInfoMapper;
import com.api.project.service.UserInterfaceInfoService;
import com.api.common.model.entity.UserInterfaceInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }

        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于 0");
        }

    }

    @Override
    @Transactional
    public Integer invokeCount(long interfaceInfoId, long userId) {
        // 判断合法
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId",userId);
        queryWrapper.eq("interfaceInfoId",interfaceInfoId);
        UserInterfaceInfo userInterfaceInfo = this.getOne(queryWrapper);
        int version = userInterfaceInfo.getVersion();
        int leftNum = userInterfaceInfo.getLeftNum();
        if(leftNum<=0){
            log.error("接口剩余调用次数不足");
            return null;
        }

        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);
        updateWrapper.eq("version",version);
        updateWrapper.gt("leftNum", 0);
        updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1, version = version+1");
        boolean update = this.update(updateWrapper);
        if(update){
            return leftNum - 1;
        }else{
            return null;
        }

    }

    @Override
    public boolean rollBackCount(long interfaceInfoId, long userId, int num){
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UpdateWrapper<UserInterfaceInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("interfaceInfoId", interfaceInfoId);
        updateWrapper.eq("userId", userId);


        updateWrapper.setSql(String.format("leftNum = leftNum + %d, totalNum = totalNum - %d", num, num));
        return this.update(updateWrapper);
    }


    @Override
    public int getLeftNum(long interfaceInfoId, long userId) {
        // 判断合法
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<UserInterfaceInfo> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId);
        lambdaQueryWrapper.eq(UserInterfaceInfo::getUserId, userId);
        UserInterfaceInfo one = this.getOne(lambdaQueryWrapper);
        return one.getLeftNum();
    }
}




