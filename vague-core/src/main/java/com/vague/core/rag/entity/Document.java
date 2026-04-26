package com.vague.core.chat.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("document")
public class Document extends BaseEntity {
    @TableField("kb_id")
    private String kbId;

    private String filename;
    private String filetype;

    private Long size;

    private String metadata;
}
