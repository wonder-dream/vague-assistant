package com.vague.core.rag.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vague.core.chat.common.entity.BaseEntity;
import com.vague.core.rag.typehandler.PgVectorTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName("chunk_bge_m3")
public class ChunkBgeM3 extends BaseEntity {

    /** 所属知识库ID */
    @TableField("kb_id")
    private String kbId;

    /** 来源文档ID */
    @TableField("doc_id")
    private String docId;

    /** 分片文本内容 */
    private String content;

    private String metadata;

    /** 向量（1024维，配合 TypeHandler 自动转换） */
    @TableField(typeHandler = PgVectorTypeHandler.class)
    private float[] embedding;

    /** 非数据库字段：查询时的相似度距离（越小越相似） */
    @TableField(exist = false)
    private Double distance;
}
