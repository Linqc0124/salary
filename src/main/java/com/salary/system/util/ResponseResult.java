package com.salary.system.util;

import lombok.Data;

/**
 * 响应结果工具类
 */
@Data
public class ResponseResult<T> {
    /**
     * 状态码
     */
    private Integer code;
    
    /**
     * 消息
     */
    private String message;
    
    /**
     * 数据
     */
    private T data;
    
    /**
     * 成功
     */
    public static <T> ResponseResult<T> success() {
        return success(null);
    }
    
    /**
     * 成功
     */
    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    
    /**
     * 失败
     */
    public static <T> ResponseResult<T> error(String message) {
        return error(500, message);
    }
    
    /**
     * 失败
     */
    public static <T> ResponseResult<T> error(Integer code, String message) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
} 