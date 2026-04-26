package com.vague.web.controller;

import com.vague.core.chat.common.exception.BusinessException;
import com.vague.core.chat.common.result.Result;
import com.vague.core.rag.entity.Document;
import com.vague.core.rag.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {
    private final DocumentService documentService;

    // 上传文档并触发切片 + 向量化入库
    // 目前是同步阻塞的，文档较大时前端需要等待（后续看改不改异步）
    @PostMapping("/upload")
    public Result<Void> upload(@RequestParam("file") MultipartFile file,
                               @RequestParam("kbId") String kbId) throws IOException {
        if (file.isEmpty()){
            throw new BusinessException(400, "文件不能为空");
        }
        documentService.importFile(file, kbId);
        return Result.ok(null);
    }

    // 查询对应知识库下的全部文档
    @GetMapping("/list")
    public Result<List<Document>> list(@RequestParam String kbId){
        List<Document> docs = documentService.lambdaQuery()
                .eq(Document::getKbId, kbId)
                .orderByDesc(Document::getCreatedAt)
                .list();
        return Result.ok(docs);
    }

    // 删除文档
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id){
        boolean removed =  documentService.removeById(id);
        if (!removed){
            throw new BusinessException(404, "文档不存在");
        }
        return Result.ok(null);
    }

    // 状态查询
    @GetMapping("/{id}/status")
    public Result<String> status(@PathVariable String id) {
        Document doc = documentService.getById(id);
        if (doc == null) throw new BusinessException(404, "文档不存在");
        return Result.ok(doc.getMetadata());
    }
}
