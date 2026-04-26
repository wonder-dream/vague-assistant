package com.vague.web.handler;

import com.vague.core.chat.common.result.Result;
import com.vague.core.chat.common.exception.BusinessException;
import com.vague.core.chat.common.exception.DeepSeekException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 业务异常（已知错误，前端需要展示给用户）
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常: code={}, message={}", e.getCode(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * DeepSeek 调用异常（外部服务问题）
     */
    @ExceptionHandler(DeepSeekException.class)
    public Result<Void> handleDeepSeekException(DeepSeekException e) {
        log.error("AI 服务异常: httpStatus={}, message={}", e.getHttpStatus(), e.getMessage(), e);
        return Result.error(e.getCode(), "AI 服务暂时不可用，请稍后重试");
    }

    /**
     * 数据库异常
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public Result<Void> handleDataIntegrityException(org.springframework.dao.DataIntegrityViolationException e) {
        log.error("数据库异常", e);
        return Result.error(500, "数据操作失败，请检查输入");
    }

    /**
     * 其他未知异常（兜底）
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统未知异常", e);
        return Result.error(500, "系统繁忙，请稍后重试");
    }
}
