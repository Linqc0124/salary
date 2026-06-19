package com.salary.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 审计日志实体类
 */
@Data
@TableName("t_audit_log")
public class AuditLog {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 操作用户ID，为空表示未登录用户
     */
    private Integer userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 操作类型
     */
    private String operation;
    
    /**
     * 请求方法
     */
    private String method;
    
    /**
     * 请求参数
     */
    private String params;
    
    /**
     * IP地址
     */
    private String ip;
    
    /**
     * 操作时间
     */
    private Date createTime;
    
    /**
     * 操作状态：0-失败，1-成功
     */
    private Integer status;
    
    /**
     * 操作消息
     */
    private String message;
    
    /**
     * 日志HMAC值（HMAC-SM3）
     */
    private String hmac;
} 