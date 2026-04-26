package com.vague.core.rag.chunker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextChunker {
    private static final int CHUNK_SIZE = 500;  // 切片大小
    private static final int OVERLAP = 50;      // 重叠大小
    private static final int MAX_SINGLE_CHUNK = CHUNK_SIZE * 2;     // 最大切片
    private static final String SENTENCE_END_CHARS = "。！？.!?\n";  // 按句子切片

    public static List<String> chunk(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }
        text = text.trim();

        // 1.切成句子流
        List<String> sentences = splitToSentence(text);

        List<String> chunks = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        for (String s : sentences) {

            // 1.巨大句子
            if (s.length() > MAX_SINGLE_CHUNK) {
                if (!buffer.isEmpty()) {
                    chunks.add(buffer.toString());
                    buffer.setLength(0);
                }
                chunks.addAll(forceSplit(s));
                continue;
            }

            // 2.加入这个句子会撑爆
            if (buffer.length() + s.length() > CHUNK_SIZE && !buffer.isEmpty()) {
                chunks.add(buffer.toString());
                String tail = buffer.length() > OVERLAP
                        ? buffer.substring(buffer.length() - OVERLAP)
                        : buffer.toString();
                buffer.setLength(0);
                buffer.append(tail);

            }

            // 3.正常添加
            buffer.append(s);
        }
        if (!buffer.isEmpty()) {
            chunks.add(buffer.toString());
        }
        return chunks;
    }

    private static List<String> forceSplit(String sentence) {
        List<String> result = new ArrayList<>();
        int step = CHUNK_SIZE - OVERLAP;
        for (int i = 0; i < sentence.length(); i +=  step) {
            int end = Math.min(i + CHUNK_SIZE, sentence.length());
            result.add(sentence.substring(i, end));
            if (end == sentence.length()) break;
        }
        return result;
    }

    private static List<String> splitToSentence(String text) {
        List<String> sentences = new ArrayList<>();
        StringBuilder cur = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            cur.append(ch);

            if (SENTENCE_END_CHARS.indexOf(ch) >= 0) {
                sentences.add(cur.toString());
                cur.setLength(0);
            }
        }
        if (!cur.isEmpty()) {
            sentences.add(cur.toString());
        }
        return sentences;
    }

}
