package com.vague.core.rag.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vague.core.rag.entity.Document;
import com.vague.core.rag.mapper.DocumentMapper;
import com.vague.core.rag.service.DocumentProcessService;
import com.vague.core.rag.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {

    @Lazy
    @Autowired
    private DocumentProcessService documentProcessService;

    @Override
    public String importFile(MultipartFile file, String kbId) throws IOException {
        byte[] bytes = file.getBytes();
        String filename = file.getOriginalFilename();

        String filetype = "";
        if (filename != null && filename.contains(".")) {
            filetype = filename.substring(filename.lastIndexOf(".") + 1);
        }

        Document doc = new Document();
        doc.setKbId(kbId);
        doc.setFilename(filename);
        doc.setFiletype(filetype);
        doc.setSize(file.getSize());
        doc.setMetadata("{\"status\":\"processing\"}");
        save(doc);

        // 异步处理，立即返回 docId
        documentProcessService.process(doc.getId(), kbId, bytes);

        return doc.getId();
    }
}

