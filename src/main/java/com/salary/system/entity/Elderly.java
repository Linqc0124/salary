package com.salary.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 老人实体类
 */
@Data
@TableName("t_elderly")
public class Elderly {
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
     * 老人姓名
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
     * 是否独生子女：0-否，1-是
     */
    private Integer isSoleSupport;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}