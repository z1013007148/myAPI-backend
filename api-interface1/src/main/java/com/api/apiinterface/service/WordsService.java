package com.api.apiinterface.service;

import com.api.common.model.entity.Words;
import com.baomidou.mybatisplus.extension.service.IService;


public interface WordsService extends IService<Words> {
    Words getRandom();
}
