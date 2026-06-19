package com.salary.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 专项附加扣除实体类
 */
@Data
@TableName("t_special_deduction")
public class SpecialDeduction {
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
     * 年份
     */
    private Integer year;
    
    /**
     * 子女教育
     */
    private BigDecimal childrenEducation;
    
    /**
     * 继续教育
     */
    private BigDecimal continuingEducation;
    
    /**
     * 住房贷款利息
     */
    private BigDecimal housingLoan;
    
    /**
     * 住房租金
     */
    private BigDecimal housingRent;
    
    /**
     * 赡养老人
     */
    private BigDecimal elderlyCare;
    
    /**
     * 大病医疗
     */
    private BigDecimal medicalExpense;
    
    /**
     * 婴幼儿照护
     */
    private BigDecimal childCare;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
} 