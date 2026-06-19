package com.salary.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 工资配置实体类
 */
@Data
@TableName("t_salary_config")
public class SalaryConfig {
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
     * 基本工资
     */
    private BigDecimal baseSalary;
    
    /**
     * 岗位工资
     */
    private BigDecimal positionSalary;
    
    /**
     * 午餐补贴
     */
    private BigDecimal lunchSubsidy;
    
    /**
     * 全勤奖
     */
    private BigDecimal fullAttendanceBonus;
    
    /**
     * 加班小时工资
     */
    private BigDecimal overtimeHourlyRate;
    
    /**
     * 状态：0-无效，1-有效
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