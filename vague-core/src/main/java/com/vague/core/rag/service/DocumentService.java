package com.vague.core.rag.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vague.core.rag.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface DocumentService extends IService<Document> {
    String importFile(MultipartFile file, String kbId)  throws IOException;
}
