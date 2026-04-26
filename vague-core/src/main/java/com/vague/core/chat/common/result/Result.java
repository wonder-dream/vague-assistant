package com.vague.core.chat.common.result;

import lombok.Data;

@Data
public class Result<T> {
    private int  code;
    private String msg;
    private T data;

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<T>();
        result.setCode(200);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(int code, String msg) {
        Result<T> result = new Result<T>();
        result.setCode(code);
        result.setMsg(msg);
        return result;
    }
}
