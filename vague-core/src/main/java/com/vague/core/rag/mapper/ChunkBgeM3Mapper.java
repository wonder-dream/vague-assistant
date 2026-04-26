package com.vague.core.rag.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vague.core.rag.entity.ChunkBgeM3;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ChunkBgeM3Mapper extends BaseMapper<ChunkBgeM3> {

    List<ChunkBgeM3> searchByVector(@Param("kbId") String kbId,
                                    @Param("embedding") String embedding,
                                    @Param("limit") int limit);

    // 带文档过滤的动态版本
    List<ChunkBgeM3> searchByVectorWithDocFilter(@Param("kbId") String kbId,
                                                 @Param("embedding") String embedding,
                                                 @Param("documentId") String documentId,
                                                 @Param("limit") int limit);

    List<ChunkBgeM3> searchByVectorMultiKb(String kbIds, String embedding, int limit);
}
