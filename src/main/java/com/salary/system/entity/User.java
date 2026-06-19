package com.salary.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 系统用户实体类
 */
@Data
@TableName("t_user")
public class User {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码（SM3加密）
     */
    private String password;
    
    /**
     * 真实姓名
     */
    private String realName;
    
    /**
     * 角色：ADMIN-系统管理员，HR-人事管理员，FINANCE-财务管理员，MANAGER-总经理，AUDIT-审计员
     */
    private String role;
    
    /**
     * 最后密码重置时间
     */
    private Date lastPasswordResetDate;
    
    /**
     * 登录失败次数
     */
    private Integer loginFailCount;
    
    /**
     * 锁定时间
     */
    private Date lockedTime;
    
    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 