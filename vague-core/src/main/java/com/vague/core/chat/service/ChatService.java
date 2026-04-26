package com.vague.core.chat.service;

import com.vague.core.chat.entity.dto.ChatRequestMessage;

import java.io.IOException;
import java.util.function.Consumer;

public interface ChatService {
    String talk(ChatRequestMessage chatRequestMessage) throws IOException, InterruptedException;
    void streamTalk(ChatRequestMessage chatRequestMessage, Consumer<String> onContent)
            throws IOException, InterruptedException;
}
