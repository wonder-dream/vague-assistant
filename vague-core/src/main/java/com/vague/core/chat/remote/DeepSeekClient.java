package com.vague.core.chat.remote;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vague.core.chat.api.DeepSeekChatRequest;
import com.vague.core.chat.api.DeepSeekChatResponse;
import com.vague.core.chat.api.DeepSeekMessage;
import com.vague.core.chat.api.DeepSeekStreamChunk;
import com.vague.core.chat.api.ToolDefinition;
import com.vague.core.chat.common.exception.DeepSeekException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class DeepSeekClient {
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create();

    @Value("${deepseek.api-key}")
    private String apiKey;
    @Value("${deepseek.api-url}")
    private String apiUrl;

    public String chat(List<DeepSeekMessage> messages) throws IOException, InterruptedException {
        return chat(messages, null, Map.of());
    }

    public String chat(List<DeepSeekMessage> messages,
                       List<ToolDefinition> tools,
                       Map<String, Function<String, String>> toolRegistry) throws IOException, InterruptedException {
        List<DeepSeekMessage> context = new ArrayList<>(messages);

        for (int i = 0; i < 5; i++) {
            DeepSeekChatRequest request = new DeepSeekChatRequest();
            request.setMessages(context);
            if (tools != null && !tools.isEmpty()) request.setTools(tools);

            String responseBody = sendPost(gson.toJson(request));
            DeepSeekChatResponse response = gson.fromJson(responseBody, DeepSeekChatResponse.class);
            DeepSeekChatResponse.Choice choice = response.getChoices().get(0);
            DeepSeekMessage assistantMsg = choice.getMessage();

            if (!"tool_calls".equals(choice.getFinishReason()) || assistantMsg.getToolCalls() == null) {
                return assistantMsg.getContent();
            }

            context.add(assistantMsg);

            for (DeepSeekMessage.ToolCall tc : assistantMsg.getToolCalls()) {
                String toolName = tc.getFunction().getName();
                String result = toolRegistry.getOrDefault(toolName, args -> "工具不存在: " + toolName)
                        .apply(tc.getFunction().getArguments());
                DeepSeekMessage toolResult = new DeepSeekMessage("tool", result);
                toolResult.setToolCallId(tc.getId());
                context.add(toolResult);
            }
        }
        return "工具调用轮次超限";
    }

    public void streamChat(List<DeepSeekMessage> messages, Consumer<String> onContent) {
        try {
            // 1.组装请求， stream=true
            DeepSeekChatRequest chatRequest = new DeepSeekChatRequest();
            chatRequest.setMessages(messages);
            chatRequest.setStream(true);

            String json = gson.toJson(chatRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            // 2.使用 ofInputStream 代替 ofString 接收
            HttpResponse<InputStream> response = CLIENT.send(
                    request,
                    HttpResponse
                            .BodyHandlers
                            .ofInputStream()
            );

            if (response.statusCode() != 200) {
                String errorBody = response.body().toString();
                throw new DeepSeekException(
                        response.statusCode(),
                        "HTTP " + response.statusCode() + ", 响应: " + errorBody
                );
            }

            // 3.逐行读取 SSE 格式
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(response.body(),
                            StandardCharsets.UTF_8)
            );
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("data: ")) {
                    String data = line.substring(6);

                    if ("[DONE]".equals(data)) {
                        break;          // 流结束
                    }

                    // 4.解析 delta.content
                    DeepSeekStreamChunk chunk = gson.fromJson(data, DeepSeekStreamChunk.class);
                    String content = chunk.getDeltaContent();

                    if (content != null && !content.isEmpty()) {
                        onContent.accept(content);
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new DeepSeekException(-1, "网络请求失败: " + e.getMessage());
        }
    }

    private String extractReply(String responseBody) {
        DeepSeekChatResponse response = gson.fromJson(responseBody, DeepSeekChatResponse.class);
        return response.getChoices().get(0).getMessage().getContent();
    }

    private String sendPost(String json) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            String errorBody = response.body();
            throw new DeepSeekException(
                    response.statusCode(),
                    "HTTP " + response.statusCode() + ", 响应: " + errorBody
            );
        }
        return  response.body();
    }

}
