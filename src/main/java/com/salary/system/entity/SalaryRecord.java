package com.salary.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 月工资记录实体类
 */
@Data
@TableName("t_salary_record")
public class SalaryRecord {
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
     * 部门ID
     */
    private Integer departmentId;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 月份
     */
    private Integer month;

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
     * 加班工资
     */
    private BigDecimal overtimeSalary;

    /**
     * 其他奖金
     */
    private BigDecimal otherBonus;

    /**
     * 应发工资（税前）
     */
    private BigDecimal grossSalary;

    /**
     * 社保扣除
     */
    private BigDecimal socialInsurance;

    /**
     * 公积金扣除
     */
    private BigDecimal housingFund;

    /**
     * 专项附加扣除
     */
    private BigDecimal specialDeduction;

    /**
     * 个人所得税
     */
    private BigDecimal taxAmount;

    /**
     * 缺勤扣款
     */
    private BigDecimal absenceDeduction;

    /**
     * 其他扣款
     */
    private BigDecimal otherDeduction;

    /**
     * 实发工资（税后）
     */
    private BigDecimal netSalary;

    /**
     * 备注
     */
    private String note;

    /**
     * 状态：0-作废，1-正常
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

    @TableField(exist = false)
    private String employeeName;
    @TableField(exist = false)
    private String departmentName;
}
