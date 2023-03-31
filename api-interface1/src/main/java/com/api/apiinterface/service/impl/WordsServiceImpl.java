package com.api.apiinterface.service.impl;

import com.api.apiinterface.mapper.WordsMapper;
import com.api.apiinterface.service.WordsService;
import com.api.common.model.entity.Words;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WordsServiceImpl extends ServiceImpl<WordsMapper, Words> implements WordsService {
    @Resource
    private WordsMapper wordsMapper;

    @Override
    public Words getRandom(){
        return wordsMapper.getRandom();
    }

}
