package com.vague.web.controller;

import com.vague.core.chat.common.result.Result;
import com.vague.core.chat.entity.dto.ChatRequestMessage;
import com.vague.core.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/talk")
    public Result<String> talk(@RequestBody ChatRequestMessage chatRequestMessage)
            throws IOException, InterruptedException {
        String reply = chatService.talk(chatRequestMessage);
        return Result.ok(reply);
    }

    @PostMapping("/stream")
    public SseEmitter streamChat(@RequestBody ChatRequestMessage chatRequestMessage) {
        // 0L 表示不会超时
        SseEmitter emitter = new SseEmitter(0L);

        // 异步执行。不阻塞 HTTP 线程
        CompletableFuture.runAsync(() -> {
            try {
                chatService.streamTalk(chatRequestMessage, token -> {
                    try {
                        emitter.send(token);    // 逐字推送给前端
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                });

                // 正常结束
                emitter.complete();

            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        return emitter; // 立即返回，保持连接打开
    }
}
