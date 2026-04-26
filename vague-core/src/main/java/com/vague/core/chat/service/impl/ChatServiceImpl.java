package com.vague.core.chat.service.impl;

import com.vague.core.chat.common.Constant;
import com.vague.core.chat.common.exception.BusinessException;
import com.vague.core.chat.entity.ChatMessage;
import com.vague.core.chat.api.DeepSeekMessage;
import com.vague.core.chat.entity.ChatSession;
import com.vague.core.chat.entity.dto.ChatRequestMessage;
import com.vague.core.chat.remote.DeepSeekClient;
import com.vague.core.chat.service.AgentService;
import com.vague.core.chat.service.ChatMessageService;
import com.vague.core.chat.service.ChatService;
import com.vague.core.chat.service.ChatSessionService;
import com.vague.core.chat.api.ToolDefinition;
import com.vague.core.chat.tool.ToolExecutor;
import com.vague.core.rag.service.RagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final RagService ragService;
    private final ChatSessionService chatSessionService;
    private final ChatMessageService chatMessageService;
    private final AgentService agentService;
    private final DeepSeekClient deepSeekClient;
    private final List<ToolExecutor> toolExecutors;

    @Override
    public String talk(ChatRequestMessage chatRequestMessage) throws IOException, InterruptedException {
        // 参数校验
        if (chatRequestMessage.getContent() == null || chatRequestMessage.getContent().trim().isEmpty()) {
            throw new BusinessException(400, "消息内容不能为空");
        }

        // 1.获取用户输入的信息
        String sessionId = chatRequestMessage.getSessionId();
        String agentId = chatRequestMessage.getAgentId();
        String userContent = chatRequestMessage.getContent();

        // 2.如果没有会话，创建新会话
        if (sessionId == null || sessionId.trim().isEmpty() ) {
            sessionId = chatSessionService.createSession(chatRequestMessage);
        }  else {
            // 验证 Session 是否存在
            ChatSession session = chatSessionService.getById(sessionId);
            if (session == null) {
                throw new BusinessException(404, "会话不存在: " + sessionId);
            }
        }

        // 3.获取该会话的历史记录
        List<ChatMessage> dbHistory = chatMessageService.getMessageHistory(sessionId, Constant.LIMIT);
        List<DeepSeekMessage> history = dbHistory.stream()
                .map(e -> new DeepSeekMessage(e.getRole(), e.getContent()))
                .toList();

        // 4. RAG 检索
        List<String> kbIds = agentService.getAllowedKbIds(agentId);
        String ragContext = kbIds.isEmpty() ? "" : ragService.retrieveContext(userContent, kbIds, 5);

        // 5.拼接出完整的会话信息
        List<DeepSeekMessage> messages = buildMessages(agentId, userContent, history, ragContext);

        // 6.将本次对话信息插入表中
        chatMessageService.insert(sessionId, "user", userContent);

        // 6.调用 API 发送 HTTP 请求给大模型并处理响应获取回复
        List<ToolDefinition> tools = toolExecutors.stream().map(ToolExecutor::definition).collect(Collectors.toList());
        Map<String, java.util.function.Function<String, String>> registry = toolExecutors.stream()
                .collect(Collectors.toMap(ToolExecutor::name, t -> t::execute));
        String reply = deepSeekClient.chat(messages, tools, registry);
        chatMessageService.insert(sessionId, "assistant", reply);
        return reply;
    }

    public void streamTalk(ChatRequestMessage chatRequestMessage, Consumer<String> onContent)
            throws IOException, InterruptedException {
        // 参数校验
        if (chatRequestMessage.getContent() == null || chatRequestMessage.getContent().trim().isEmpty()) {
            throw new BusinessException(400, "消息内容不能为空");
        }

        // 1.处理 Session
        String sessionId = chatRequestMessage.getSessionId();
        String agentId = chatRequestMessage.getAgentId();
        String userContent = chatRequestMessage.getContent();
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = chatSessionService.createSession(chatRequestMessage);
        }

        // 2.查询历史消息
        List<ChatMessage> dbHistory = chatMessageService.getMessageHistory(sessionId, Constant.LIMIT);
        List<DeepSeekMessage> history = dbHistory.stream()
                .map(e -> new DeepSeekMessage(e.getRole(), e.getContent()))
                .toList();

        // 3. RAG 检索
        List<String> kbIds = agentService.getAllowedKbIds(agentId);
        String ragContext = kbIds.isEmpty() ? "" : ragService.retrieveContext(userContent, kbIds, 5);

        // 4.拼接消息
        List<DeepSeekMessage> messages = buildMessages(agentId, userContent, history, ragContext);

        // 5.保存用户信息
        chatMessageService.insert(sessionId, "user", userContent);

        // 5.流式调用
        StringBuilder fullReply = new StringBuilder();

        deepSeekClient.streamChat(messages, token -> {
            fullReply.append(token);
            onContent.accept(token);    // 把字推送给 controller
        });

        // 6.流结束后，保存完整 AI 回复到数据库
        chatMessageService.insert(sessionId, "assistant", fullReply.toString());
    }

    private List<DeepSeekMessage> buildMessages(String agentId,
                                                String userContent,
                                                List<DeepSeekMessage> history,
                                                String ragContext) {
        List<DeepSeekMessage> messages = new ArrayList<>();

        messages.add(new DeepSeekMessage("system", agentService.getSystemPrompt(agentId)));

        // 有检索结果才注入
        if (ragContext != null && !ragContext.isEmpty()) {
            messages.add(new DeepSeekMessage("system",
                    "以下是从知识库检索到的相关资料，请优先基于这些资料回答；若资料不足请明确说明：\n---\n"
                    + ragContext + "\n---"));
        }

        messages.addAll(history);
        messages.add(new DeepSeekMessage("user", userContent));

        return messages;
    }
}
