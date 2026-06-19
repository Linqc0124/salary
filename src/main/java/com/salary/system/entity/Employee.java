package com.salary.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 员工实体类
 */
@Data
@TableName("t_employee")
public class Employee {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    /**
     * 员工编号
     */
    private String employeeNo;
    
    /**
     * 员工姓名
     */
    private String name;
    
    /**
     * 身份证号（SM4加密）
     */
    private String idCard;
    
    /**
     * 手机号（SM4加密）
     */
    private String phone;
    
    /**
     * 住址（SM4加密）
     */
    private String address;
    
    /**
     * 所属部门ID
     */
    private Integer departmentId;
    
    /**
     * 岗位
     */
    private String position;
    
    /**
     * 职务
     */
    private String jobTitle;
    
    /**
     * 入职日期
     */
    private Date entryDate;
    
    /**
     * 状态：0-离职，1-在职
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