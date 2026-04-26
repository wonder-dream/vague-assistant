package com.vague.core.chat.common.exception;

import lombok.Getter;

@Getter
public class DeepSeekException extends BusinessException {
    private final int httpStatus;

    public DeepSeekException(int httpStatusCode, String message) {
        super(502, "AI 服务异常" + message);
        this.httpStatus = httpStatusCode;
    }

}
