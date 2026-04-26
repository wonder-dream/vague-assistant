package com.vague.core.rag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vague.core.rag.entity.Document;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DocumentMapper extends BaseMapper<Document> {
}
