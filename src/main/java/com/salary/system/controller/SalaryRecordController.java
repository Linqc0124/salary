package com.salary.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.system.entity.*;
import com.salary.system.mapper.*;
import com.salary.system.util.ResponseResult;
import com.salary.system.util.TaxCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 工资记录控制器
 */
@Controller
@RequestMapping("/salary/record")
public class SalaryRecordController {

    @Resource
    private SalaryRecordMapper salaryRecordMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    @Resource
    private SalaryConfigMapper salaryConfigMapper;

    @Resource
    private SpecialDeductionMapper specialDeductionMapper;
    @Autowired
    private DepartmentMapper departmentMapper;

    /**
     * 工资记录管理页面
     */
    @GetMapping("")
    public String index() {
        return "salary/record/index";
    }

    /**
     * 工资记录列表
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseResult<IPage<SalaryRecord>> list(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  Integer employeeId, Integer departmentId, Integer year, Integer month) {
        LambdaQueryWrapper<SalaryRecord> queryWrapper = new LambdaQueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq(SalaryRecord::getEmployeeId, employeeId);
        }
        if (departmentId != null) {
            queryWrapper.eq(SalaryRecord::getDepartmentId, departmentId);
        }
        if (year != null) {
            queryWrapper.eq(SalaryRecord::getYear, year);
        }
        if (month != null) {
            queryWrapper.eq(SalaryRecord::getMonth, month);
        }

        queryWrapper.orderByDesc(SalaryRecord::getYear, SalaryRecord::getMonth);

        IPage<SalaryRecord> page = new Page<>(current, size);
        page = salaryRecordMapper.selectPage(page, queryWrapper);

        return ResponseResult.success(page);
    }

    /**
     * 获取员工的工资记录
     */
    @GetMapping("/employee/{employeeId}")
    @ResponseBody
    public ResponseResult<List<SalaryRecord>> getByEmployeeId(@PathVariable Integer employeeId) {
        LambdaQueryWrapper<SalaryRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalaryRecord::getEmployeeId, employeeId)
                   .orderByDesc(SalaryRecord::getYear, SalaryRecord::getMonth);
        List<SalaryRecord> records = salaryRecordMapper.selectList(queryWrapper);
        return ResponseResult.success(records);
    }

    /**
     * 添加工资记录页面
     */
    @GetMapping("/add")
    public String add() {
        return "salary/record/add";
    }

    /**
     * 添加工资记录
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseResult<Void> doAdd(SalaryRecord salaryRecord) {
        // 参数校验
        if (salaryRecord.getEmployeeId() == null) {
            return ResponseResult.error("员工不能为空");
        }
        if (salaryRecord.getYear() == null) {
            return ResponseResult.error("年份不能为空");
        }
        if (salaryRecord.getMonth() == null) {
            return ResponseResult.error("月份不能为空");
        }
        if (salaryRecord.getMonth() < 1 || salaryRecord.getMonth() > 12) {
            return ResponseResult.error("月份必须在1-12之间");
        }

        // 检查员工是否存在
        Employee employee = employeeMapper.selectById(salaryRecord.getEmployeeId());
        if (employee == null) {
            return ResponseResult.error("员工不存在");
        }

        // 设置部门ID
        salaryRecord.setDepartmentId(employee.getDepartmentId());

        // 检查是否已存在该员工该月的工资记录
        LambdaQueryWrapper<SalaryRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalaryRecord::getEmployeeId, salaryRecord.getEmployeeId())
                   .eq(SalaryRecord::getYear, salaryRecord.getYear())
                   .eq(SalaryRecord::getMonth, salaryRecord.getMonth())
                   .eq(SalaryRecord::getStatus, 1);
        if (salaryRecordMapper.selectCount(queryWrapper) > 0) {
            return ResponseResult.error("该员工该月的工资记录已存在");
        }

        // 获取员工的工资配置
        LambdaQueryWrapper<SalaryConfig> configQueryWrapper = new LambdaQueryWrapper<>();
        configQueryWrapper.eq(SalaryConfig::getEmployeeId, salaryRecord.getEmployeeId())
                         .eq(SalaryConfig::getStatus, 1);
        SalaryConfig salaryConfig = salaryConfigMapper.selectOne(configQueryWrapper);
        if (salaryConfig == null) {
            return ResponseResult.error("员工工资配置不存在，请先配置工资");
        }

        // 设置基本工资信息
        salaryRecord.setBaseSalary(salaryConfig.getBaseSalary());
        salaryRecord.setPositionSalary(salaryConfig.getPositionSalary());
        salaryRecord.setLunchSubsidy(salaryConfig.getLunchSubsidy());

        // 计算应发工资
        BigDecimal grossSalary = salaryRecord.getBaseSalary()
                .add(salaryRecord.getPositionSalary())
                .add(salaryRecord.getLunchSubsidy())
                .add(salaryRecord.getFullAttendanceBonus() != null ? salaryRecord.getFullAttendanceBonus() : BigDecimal.ZERO)
                .add(salaryRecord.getOvertimeSalary() != null ? salaryRecord.getOvertimeSalary() : BigDecimal.ZERO)
                .add(salaryRecord.getOtherBonus() != null ? salaryRecord.getOtherBonus() : BigDecimal.ZERO);
        salaryRecord.setGrossSalary(grossSalary);

        // 获取专项附加扣除
        LambdaQueryWrapper<SpecialDeduction> deductionQueryWrapper = new LambdaQueryWrapper<>();
        deductionQueryWrapper.eq(SpecialDeduction::getEmployeeId, salaryRecord.getEmployeeId())
                            .eq(SpecialDeduction::getYear, salaryRecord.getYear());
        SpecialDeduction specialDeduction = specialDeductionMapper.selectOne(deductionQueryWrapper);
        BigDecimal specialDeductionAmount = BigDecimal.ZERO;
        if (specialDeduction != null) {
            specialDeductionAmount = specialDeduction.getChildrenEducation()
                    .add(specialDeduction.getContinuingEducation())
                    .add(specialDeduction.getHousingLoan())
                    .add(specialDeduction.getHousingRent())
                    .add(specialDeduction.getElderlyCare())
                    .add(specialDeduction.getMedicalExpense())
                    .add(specialDeduction.getChildCare());
        }
        salaryRecord.setSpecialDeduction(specialDeductionAmount);

        // 计算个人所得税
        BigDecimal socialInsurance = salaryRecord.getSocialInsurance() != null ? salaryRecord.getSocialInsurance() : BigDecimal.ZERO;
        BigDecimal housingFund = salaryRecord.getHousingFund() != null ? salaryRecord.getHousingFund() : BigDecimal.ZERO;

        // 获取当年前几个月的累计应纳税所得额和已缴税款
        BigDecimal previousTaxableIncome = BigDecimal.ZERO;
        BigDecimal previousTaxPaid = BigDecimal.ZERO;
        if (salaryRecord.getMonth() > 1) {
            LambdaQueryWrapper<SalaryRecord> prevQueryWrapper = new LambdaQueryWrapper<>();
            prevQueryWrapper.eq(SalaryRecord::getEmployeeId, salaryRecord.getEmployeeId())
                           .eq(SalaryRecord::getYear, salaryRecord.getYear())
                           .lt(SalaryRecord::getMonth, salaryRecord.getMonth())
                           .eq(SalaryRecord::getStatus, 1);
            List<SalaryRecord> previousRecords = salaryRecordMapper.selectList(prevQueryWrapper);

            for (SalaryRecord prev : previousRecords) {
                // 计算应纳税所得额 = 应发工资 - 五险一金 - 起征点(5000) - 专项附加扣除
                BigDecimal prevSocialInsurance = prev.getSocialInsurance() != null ? prev.getSocialInsurance() : BigDecimal.ZERO;
                BigDecimal prevHousingFund = prev.getHousingFund() != null ? prev.getHousingFund() : BigDecimal.ZERO;
                BigDecimal prevSpecialDeduction = prev.getSpecialDeduction() != null ? prev.getSpecialDeduction() : BigDecimal.ZERO;

                BigDecimal taxableIncome = prev.getGrossSalary()
                        .subtract(prevSocialInsurance)
                        .subtract(prevHousingFund)
                        .subtract(new BigDecimal("5000"))
                        .subtract(prevSpecialDeduction);

                if (taxableIncome.compareTo(BigDecimal.ZERO) > 0) {
                    previousTaxableIncome = previousTaxableIncome.add(taxableIncome);
                }

                BigDecimal taxPaid = prev.getTaxAmount() != null ? prev.getTaxAmount() : BigDecimal.ZERO;
                previousTaxPaid = previousTaxPaid.add(taxPaid);
            }
        }

        BigDecimal taxAmount = TaxCalculator.calculateTax(
                grossSalary,
                socialInsurance,
                housingFund,
                specialDeductionAmount,
                salaryRecord.getMonth(),
                previousTaxableIncome,
                previousTaxPaid
        );
        salaryRecord.setTaxAmount(taxAmount);

        // 计算实发工资
        BigDecimal absenceDeduction = salaryRecord.getAbsenceDeduction() != null ? salaryRecord.getAbsenceDeduction() : BigDecimal.ZERO;
        BigDecimal otherDeduction = salaryRecord.getOtherDeduction() != null ? salaryRecord.getOtherDeduction() : BigDecimal.ZERO;

        BigDecimal netSalary = grossSalary
                .subtract(socialInsurance)
                .subtract(housingFund)
                .subtract(taxAmount)
                .subtract(absenceDeduction)
                .subtract(otherDeduction);
        salaryRecord.setNetSalary(netSalary.setScale(2, RoundingMode.HALF_UP));

        // 设置默认值
        salaryRecord.setStatus(1);
        salaryRecord.setCreateTime(new Date());
        salaryRecord.setUpdateTime(new Date());

        // 保存工资记录
        salaryRecordMapper.insert(salaryRecord);

        return ResponseResult.success();
    }

    /**
     * 编辑工资记录页面
     */
    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseResult edit(@PathVariable Integer id, Model model) {
        SalaryRecord salaryRecord = salaryRecordMapper.selectById(id);
        model.addAttribute("salaryRecord", salaryRecord);

        // 获取员工信息
        if (salaryRecord != null && salaryRecord.getEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(salaryRecord.getEmployeeId());
            model.addAttribute("employee", employee);
        }

        return ResponseResult.success(salaryRecord);
    }

    /**
     * 编辑工资记录
     */
    @PostMapping("/edit")
    @ResponseBody
    public ResponseResult<Void> doEdit(SalaryRecord salaryRecord) {
        // 参数校验
        if (salaryRecord.getId() == null) {
            return ResponseResult.error("工资记录ID不能为空");
        }

        // 检查工资记录是否存在
        SalaryRecord existRecord = salaryRecordMapper.selectById(salaryRecord.getId());
        if (existRecord == null) {
            return ResponseResult.error("工资记录不存在");
        }

        // 重新计算应发工资
        BigDecimal grossSalary = salaryRecord.getBaseSalary()
                .add(salaryRecord.getPositionSalary())
                .add(salaryRecord.getLunchSubsidy())
                .add(salaryRecord.getFullAttendanceBonus() != null ? salaryRecord.getFullAttendanceBonus() : BigDecimal.ZERO)
                .add(salaryRecord.getOvertimeSalary() != null ? salaryRecord.getOvertimeSalary() : BigDecimal.ZERO)
                .add(salaryRecord.getOtherBonus() != null ? salaryRecord.getOtherBonus() : BigDecimal.ZERO);
        salaryRecord.setGrossSalary(grossSalary);

        // 重新计算个人所得税
        BigDecimal socialInsurance = salaryRecord.getSocialInsurance() != null ? salaryRecord.getSocialInsurance() : BigDecimal.ZERO;
        BigDecimal housingFund = salaryRecord.getHousingFund() != null ? salaryRecord.getHousingFund() : BigDecimal.ZERO;
        BigDecimal specialDeductionAmount = salaryRecord.getSpecialDeduction() != null ? salaryRecord.getSpecialDeduction() : BigDecimal.ZERO;

        // 获取当年前几个月的累计应纳税所得额和已缴税款
        BigDecimal previousTaxableIncome = BigDecimal.ZERO;
        BigDecimal previousTaxPaid = BigDecimal.ZERO;
        if (salaryRecord.getMonth() > 1) {
            LambdaQueryWrapper<SalaryRecord> prevQueryWrapper = new LambdaQueryWrapper<>();
            prevQueryWrapper.eq(SalaryRecord::getEmployeeId, salaryRecord.getEmployeeId())
                           .eq(SalaryRecord::getYear, salaryRecord.getYear())
                           .lt(SalaryRecord::getMonth, salaryRecord.getMonth())
                           .eq(SalaryRecord::getStatus, 1);
            List<SalaryRecord> previousRecords = salaryRecordMapper.selectList(prevQueryWrapper);

            for (SalaryRecord prev : previousRecords) {
                // 计算应纳税所得额 = 应发工资 - 五险一金 - 起征点(5000) - 专项附加扣除
                BigDecimal prevSocialInsurance = prev.getSocialInsurance() != null ? prev.getSocialInsurance() : BigDecimal.ZERO;
                BigDecimal prevHousingFund = prev.getHousingFund() != null ? prev.getHousingFund() : BigDecimal.ZERO;
                BigDecimal prevSpecialDeduction = prev.getSpecialDeduction() != null ? prev.getSpecialDeduction() : BigDecimal.ZERO;

                BigDecimal taxableIncome = prev.getGrossSalary()
                        .subtract(prevSocialInsurance)
                        .subtract(prevHousingFund)
                        .subtract(new BigDecimal("5000"))
                        .subtract(prevSpecialDeduction);

                if (taxableIncome.compareTo(BigDecimal.ZERO) > 0) {
                    previousTaxableIncome = previousTaxableIncome.add(taxableIncome);
                }

                BigDecimal taxPaid = prev.getTaxAmount() != null ? prev.getTaxAmount() : BigDecimal.ZERO;
                previousTaxPaid = previousTaxPaid.add(taxPaid);
            }
        }

        BigDecimal taxAmount = TaxCalculator.calculateTax(
                grossSalary,
                socialInsurance,
                housingFund,
                specialDeductionAmount,
                salaryRecord.getMonth(),
                previousTaxableIncome,
                previousTaxPaid
        );
        salaryRecord.setTaxAmount(taxAmount);

        // 重新计算实发工资
        BigDecimal absenceDeduction = salaryRecord.getAbsenceDeduction() != null ? salaryRecord.getAbsenceDeduction() : BigDecimal.ZERO;
        BigDecimal otherDeduction = salaryRecord.getOtherDeduction() != null ? salaryRecord.getOtherDeduction() : BigDecimal.ZERO;

        BigDecimal netSalary = grossSalary
                .subtract(socialInsurance)
                .subtract(housingFund)
                .subtract(taxAmount)
                .subtract(absenceDeduction)
                .subtract(otherDeduction);
        salaryRecord.setNetSalary(netSalary.setScale(2, RoundingMode.HALF_UP));

        // 设置不需要修改的字段
        salaryRecord.setEmployeeId(existRecord.getEmployeeId());
        salaryRecord.setDepartmentId(existRecord.getDepartmentId());
        salaryRecord.setYear(existRecord.getYear());
        salaryRecord.setMonth(existRecord.getMonth());
        salaryRecord.setCreateTime(existRecord.getCreateTime());
        salaryRecord.setUpdateTime(new Date());

        // 更新工资记录
        salaryRecordMapper.updateById(salaryRecord);

        return ResponseResult.success();
    }

    /**
     * 作废工资记录
     */
    @PostMapping("/void/{id}")
    @ResponseBody
    public ResponseResult<Void> voidRecord(@PathVariable Integer id) {
        // 检查工资记录是否存在
        SalaryRecord salaryRecord = salaryRecordMapper.selectById(id);
        if (salaryRecord == null) {
            return ResponseResult.error("工资记录不存在");
        }

        // 作废工资记录
        salaryRecord.setStatus(0);
        salaryRecord.setUpdateTime(new Date());
        salaryRecordMapper.updateById(salaryRecord);

        return ResponseResult.success();
    }

    /**
     * 查看工资记录详情
     */
    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseResult detail(@PathVariable Integer id, Model model) {
        SalaryRecord salaryRecord = salaryRecordMapper.selectById(id);
        model.addAttribute("salaryRecord", salaryRecord);

        // 获取员工信息
        if (salaryRecord != null && salaryRecord.getEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(salaryRecord.getEmployeeId());
            model.addAttribute("employee", employee);
            Integer departmentId = employee.getDepartmentId();
            Department department = departmentMapper.selectById(departmentId);
            salaryRecord.setDepartmentName(department.getName());
            salaryRecord.setEmployeeName(employee.getName());
        }
        return ResponseResult.success(salaryRecord);
    }

    /**
     * 批量生成工资记录页面
     */
    @GetMapping("/batch")
    public String batch() {
        return "salary/record/batch";
    }

    /**
     * 批量生成工资记录
     */
    @PostMapping("/batch")
    @ResponseBody
    public ResponseResult<Void> doBatch(Integer year, Integer month, Integer departmentId) {
        // 参数校验
        if (year == null) {
            return ResponseResult.error("年份不能为空");
        }
        if (month == null) {
            return ResponseResult.error("月份不能为空");
        }
        if (month < 1 || month > 12) {
            return ResponseResult.error("月份必须在1-12之间");
        }

        // 查询需要生成工资记录的员工
        LambdaQueryWrapper<Employee> employeeQueryWrapper = new LambdaQueryWrapper<>();
        employeeQueryWrapper.eq(Employee::getStatus, 1); // 只查询在职员工
        if (departmentId != null) {
            employeeQueryWrapper.eq(Employee::getDepartmentId, departmentId);
        }
        List<Employee> employees = employeeMapper.selectList(employeeQueryWrapper);

        for (Employee employee : employees) {
            // 检查是否已存在该员工该月的工资记录
            LambdaQueryWrapper<SalaryRecord> recordQueryWrapper = new LambdaQueryWrapper<>();
            recordQueryWrapper.eq(SalaryRecord::getEmployeeId, employee.getId())
                             .eq(SalaryRecord::getYear, year)
                             .eq(SalaryRecord::getMonth, month)
                             .eq(SalaryRecord::getStatus, 1);
            if (salaryRecordMapper.selectCount(recordQueryWrapper) > 0) {
                continue; // 已存在，跳过
            }

            // 获取员工的工资配置
            LambdaQueryWrapper<SalaryConfig> configQueryWrapper = new LambdaQueryWrapper<>();
            configQueryWrapper.eq(SalaryConfig::getEmployeeId, employee.getId())
                             .eq(SalaryConfig::getStatus, 1);
            SalaryConfig salaryConfig = salaryConfigMapper.selectOne(configQueryWrapper);
            if (salaryConfig == null) {
                continue; // 无工资配置，跳过
            }

            // 创建工资记录
            SalaryRecord salaryRecord = new SalaryRecord();
            salaryRecord.setEmployeeId(employee.getId());
            salaryRecord.setDepartmentId(employee.getDepartmentId());
            salaryRecord.setYear(year);
            salaryRecord.setMonth(month);

            // 设置基本工资信息
            salaryRecord.setBaseSalary(salaryConfig.getBaseSalary());
            salaryRecord.setPositionSalary(salaryConfig.getPositionSalary());
            salaryRecord.setLunchSubsidy(salaryConfig.getLunchSubsidy());
            salaryRecord.setFullAttendanceBonus(salaryConfig.getFullAttendanceBonus());

            // 计算应发工资
            BigDecimal grossSalary = salaryRecord.getBaseSalary()
                    .add(salaryRecord.getPositionSalary())
                    .add(salaryRecord.getLunchSubsidy())
                    .add(salaryRecord.getFullAttendanceBonus() != null ? salaryRecord.getFullAttendanceBonus() : BigDecimal.ZERO);
            salaryRecord.setGrossSalary(grossSalary);

            // 默认社保和公积金（实际应根据员工情况计算）
            BigDecimal socialInsurance = grossSalary.multiply(new BigDecimal("0.08")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal housingFund = grossSalary.multiply(new BigDecimal("0.07")).setScale(2, RoundingMode.HALF_UP);
            salaryRecord.setSocialInsurance(socialInsurance);
            salaryRecord.setHousingFund(housingFund);

            // 获取专项附加扣除
            LambdaQueryWrapper<SpecialDeduction> deductionQueryWrapper = new LambdaQueryWrapper<>();
            deductionQueryWrapper.eq(SpecialDeduction::getEmployeeId, employee.getId())
                                .eq(SpecialDeduction::getYear, year);
            SpecialDeduction specialDeduction = specialDeductionMapper.selectOne(deductionQueryWrapper);
            BigDecimal specialDeductionAmount = BigDecimal.ZERO;
            if (specialDeduction != null) {
                specialDeductionAmount = specialDeduction.getChildrenEducation()
                        .add(specialDeduction.getContinuingEducation())
                        .add(specialDeduction.getHousingLoan())
                        .add(specialDeduction.getHousingRent())
                        .add(specialDeduction.getElderlyCare())
                        .add(specialDeduction.getMedicalExpense())
                        .add(specialDeduction.getChildCare());
            }
            salaryRecord.setSpecialDeduction(specialDeductionAmount);

            // 计算个人所得税
            // 获取当年前几个月的累计应纳税所得额和已缴税款
            BigDecimal previousTaxableIncome = BigDecimal.ZERO;
            BigDecimal previousTaxPaid = BigDecimal.ZERO;
            if (month > 1) {
                LambdaQueryWrapper<SalaryRecord> prevQueryWrapper = new LambdaQueryWrapper<>();
                prevQueryWrapper.eq(SalaryRecord::getEmployeeId, employee.getId())
                               .eq(SalaryRecord::getYear, year)
                               .lt(SalaryRecord::getMonth, month)
                               .eq(SalaryRecord::getStatus, 1);
                List<SalaryRecord> previousRecords = salaryRecordMapper.selectList(prevQueryWrapper);

                for (SalaryRecord prev : previousRecords) {
                    // 计算应纳税所得额 = 应发工资 - 五险一金 - 起征点(5000) - 专项附加扣除
                    BigDecimal prevSocialInsurance = prev.getSocialInsurance() != null ? prev.getSocialInsurance() : BigDecimal.ZERO;
                    BigDecimal prevHousingFund = prev.getHousingFund() != null ? prev.getHousingFund() : BigDecimal.ZERO;
                    BigDecimal prevSpecialDeduction = prev.getSpecialDeduction() != null ? prev.getSpecialDeduction() : BigDecimal.ZERO;

                    BigDecimal taxableIncome = prev.getGrossSalary()
                            .subtract(prevSocialInsurance)
                            .subtract(prevHousingFund)
                            .subtract(new BigDecimal("5000"))
                            .subtract(prevSpecialDeduction);

                    if (taxableIncome.compareTo(BigDecimal.ZERO) > 0) {
                        previousTaxableIncome = previousTaxableIncome.add(taxableIncome);
                    }

                    BigDecimal taxPaid = prev.getTaxAmount() != null ? prev.getTaxAmount() : BigDecimal.ZERO;
                    previousTaxPaid = previousTaxPaid.add(taxPaid);
                }
            }

            BigDecimal taxAmount = TaxCalculator.calculateTax(
                    grossSalary,
                    socialInsurance,
                    housingFund,
                    specialDeductionAmount,
                    month,
                    previousTaxableIncome,
                    previousTaxPaid
            );
            salaryRecord.setTaxAmount(taxAmount);

            // 计算实发工资
            BigDecimal netSalary = grossSalary
                    .subtract(socialInsurance)
                    .subtract(housingFund)
                    .subtract(taxAmount);
            salaryRecord.setNetSalary(netSalary.setScale(2, RoundingMode.HALF_UP));

            // 设置默认值
            salaryRecord.setStatus(1);
            salaryRecord.setCreateTime(new Date());
            salaryRecord.setUpdateTime(new Date());

            // 保存工资记录
            salaryRecordMapper.insert(salaryRecord);
        }

        return ResponseResult.success();
    }
}
