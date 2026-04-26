package com.vague.web.controller;

import com.vague.core.chat.common.exception.BusinessException;
import com.vague.core.chat.common.result.Result;
import com.vague.core.rag.entity.KnowledgeBase;
import com.vague.core.rag.entity.dto.KnowledgeBaseCreateRequest;
import com.vague.core.rag.entity.vo.KnowledgeBaseVO;
import com.vague.core.rag.service.KnowledgeBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/kb")
@RequiredArgsConstructor
public class KnowledgeBaseController {
    private final KnowledgeBaseService knowledgeBaseService;

    @PostMapping
    public Result<String> create(@RequestBody KnowledgeBaseCreateRequest request) {
        return Result.ok(knowledgeBaseService.create(request));
    }

    @GetMapping("/list")
    public Result<List<KnowledgeBaseVO>> list() {
        List<KnowledgeBaseVO> list = knowledgeBaseService.list().stream().map(knowledgeBase -> {
            KnowledgeBaseVO knowledgeBaseVO = new KnowledgeBaseVO();
            BeanUtils.copyProperties(knowledgeBase, knowledgeBaseVO);
            return knowledgeBaseVO;
        }).collect(Collectors.toList());
        return Result.ok(list);
    }

    @GetMapping("/{id}")
    public Result<KnowledgeBaseVO> get(@PathVariable String id) {
        KnowledgeBase knowledgeBase = knowledgeBaseService.getById(id);
        if (knowledgeBase == null) {
            throw new BusinessException(404, "知识库不存在");
        }
        KnowledgeBaseVO knowledgeBaseVO = new KnowledgeBaseVO();
        BeanUtils.copyProperties(knowledgeBase, knowledgeBaseVO);
        return Result.ok(knowledgeBaseVO);
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable String id, @RequestBody KnowledgeBaseCreateRequest request) {
        KnowledgeBase knowledgeBase = knowledgeBaseService.getById(id);
        if (knowledgeBase == null) {
            throw new BusinessException(404, "知识库不存在");
        }
        BeanUtils.copyProperties(request, knowledgeBase);
        knowledgeBaseService.updateById(knowledgeBase);
        return Result.ok(null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteById(@PathVariable String id) {
        boolean removed = knowledgeBaseService.removeById(id);
        if (!removed) {
            throw new BusinessException(404, "知识库不存在");
        }
        return Result.ok(null);
    }
}
