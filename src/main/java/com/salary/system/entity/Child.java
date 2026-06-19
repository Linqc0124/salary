package com.salary.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 子女实体类
 */
@Data
@TableName("t_child")
public class Child {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 员工ID
     */
    private Integer employeeId;
    
    /**
     * 子女姓名
     */
    private String name;
    
    /**
     * 身份证号（SM4加密）
     */
    private String idCard;
    
    /**
     * 关系
     */
    private String relation;
    
    /**
     * 教育阶段：PRESCHOOL-学前教育，PRIMARY-小学，JUNIOR-初中，HIGH-高中，UNIVERSITY-大学
     */
    private String educationStage;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 