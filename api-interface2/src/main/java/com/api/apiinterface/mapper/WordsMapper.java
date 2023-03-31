package com.api.apiinterface.mapper;

import com.api.common.model.entity.Words;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface WordsMapper extends BaseMapper<Words> {
    @Select("select * from myapi_db.words order by rand() limit 1")
    Words getRandom();
}
