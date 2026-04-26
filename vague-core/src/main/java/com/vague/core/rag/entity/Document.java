package com.vague.core.rag.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.vague.core.chat.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
@TableName("document")
public class Document extends BaseEntity {

    /** 所属知识库ID */
    @TableField("kb_id")
    private String kbId;

    /** 文档标题 */
    private String filename;

    /** 文件类型：pdf / txt / md / docx */
    private String filetype;

    private Long size;

    private String metadata;
    /*
    * 约定格式
    * {"status": "processing"}   // 处理中
    * {"status": "done", "chunkCount": 12}  // 完成
    * {"status": "failed", "error": "..."}  // 失败
    * */
}
