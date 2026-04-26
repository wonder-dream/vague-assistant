package com.vague.web.controller;

import com.vague.core.chat.common.exception.BusinessException;
import com.vague.core.chat.common.result.Result;
import com.vague.core.chat.entity.ChatMessage;
import com.vague.core.chat.entity.ChatSession;
import com.vague.core.chat.entity.dto.RenameSessionRequest;
import com.vague.core.chat.entity.vo.ChatMessageVO;
import com.vague.core.chat.entity.vo.ChatSessionVO;
import com.vague.core.chat.service.ChatMessageService;
import com.vague.core.chat.service.ChatSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 会话管理：列表 / 详情 / 重命名 / 删除 / 消息历史
 *
 * 注意：会话的"创建"不在这里 —— 由 ChatService.talk 在用户首次发消息时隐式创建。
 * 这里只暴露查询和管理接口。
 */
@RestController
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

    private final ChatSessionService chatSessionService;
    private final ChatMessageService chatMessageService;

    /**
     * 列出所有会话
     * 暂无用户系统，先返全部；后续接入用户后按 userId 过滤即可
     */
    @GetMapping("/list")
    public Result<List<ChatSessionVO>> list() {
        List<ChatSessionVO> list = chatSessionService.list().stream().map(session -> {
            ChatSessionVO vo = new ChatSessionVO();
            BeanUtils.copyProperties(session, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(list);
    }

    /**
     * 查询单个会话的元信息（标题、绑定的 agent 等）
     */
    @GetMapping("/{id}")
    public Result<ChatSessionVO> getById(@PathVariable String id) {
        ChatSession session = chatSessionService.getById(id);
        if (session == null) {
            throw new BusinessException(404, "会话不存在");
        }
        ChatSessionVO vo = new ChatSessionVO();
        BeanUtils.copyProperties(session, vo);
        return Result.ok(vo);
    }

    /**
     * 重命名会话标题
     */
    @PutMapping("/{id}")
    public Result<Void> rename(@PathVariable String id, @RequestBody RenameSessionRequest req) {
        ChatSession session = chatSessionService.getById(id);
        if (session == null) {
            throw new BusinessException(404, "会话不存在");
        }
        session.setTitle(req.getTitle());
        chatSessionService.updateById(session);
        return Result.ok(null);
    }

    /**
     * 删除会话
     * chat_message 表设了 ON DELETE CASCADE，删 session 时消息会被数据库级联清除
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable String id) {
        boolean removed = chatSessionService.removeById(id);
        if (!removed) {
            throw new BusinessException(404, "会话不存在");
        }
        return Result.ok(null);
    }

    /**
     * 拉会话内的历史消息，按创建时间正序
     * 默认 1000 条够前端渲染用，真要做大消息量场景再上分页
     */
    @GetMapping("/{id}/messages")
    public Result<List<ChatMessageVO>> messages(@PathVariable String id,
                                                @RequestParam(defaultValue = "1000") int limit) {
        // 先校验会话存在，避免 id 写错时静默返回空数组让人困惑
        if (chatSessionService.getById(id) == null) {
            throw new BusinessException(404, "会话不存在");
        }
        List<ChatMessageVO> vos = chatMessageService.getMessageHistory(id, limit).stream().map(m -> {
            ChatMessageVO vo = new ChatMessageVO();
            BeanUtils.copyProperties(m, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.ok(vos);
    }
}
