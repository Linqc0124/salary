package com.salary.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.system.entity.SpecialDeduction;
import com.salary.system.entity.Employee;
import com.salary.system.mapper.SpecialDeductionMapper;
import com.salary.system.mapper.EmployeeMapper;
import com.salary.system.util.ResponseResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 专项附加扣除控制器
 */
@Controller
@RequestMapping("/deduction")
public class SpecialDeductionController {

    @Resource
    private SpecialDeductionMapper specialDeductionMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 专项附加扣除管理页面
     */
    @GetMapping("")
    public String index() {
        return "deduction/index";
    }

    /**
     * 专项附加扣除列表
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseResult<IPage<SpecialDeduction>> list(@RequestParam(defaultValue = "1") Integer current,
                                                       @RequestParam(defaultValue = "10") Integer size,
                                                       Integer employeeId, Integer year) {
        LambdaQueryWrapper<SpecialDeduction> queryWrapper = new LambdaQueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq(SpecialDeduction::getEmployeeId, employeeId);
        }
        if (year != null) {
            queryWrapper.eq(SpecialDeduction::getYear, year);
        }

        IPage<SpecialDeduction> page = new Page<>(current, size);
        page = specialDeductionMapper.selectPage(page, queryWrapper);

        return ResponseResult.success(page);
    }

    /**
     * 获取员工的专项附加扣除
     */
    @GetMapping("/employee/{employeeId}")
    @ResponseBody
    public ResponseResult<List<SpecialDeduction>> getByEmployeeId(@PathVariable Integer employeeId) {
        LambdaQueryWrapper<SpecialDeduction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpecialDeduction::getEmployeeId, employeeId);
        List<SpecialDeduction> deductions = specialDeductionMapper.selectList(queryWrapper);
        return ResponseResult.success(deductions);
    }

    /**
     * 获取员工某年的专项附加扣除
     */
    @GetMapping("/employee/{employeeId}/{year}")
    @ResponseBody
    public ResponseResult<SpecialDeduction> getByEmployeeIdAndYear(@PathVariable Integer employeeId,
                                                                  @PathVariable Integer year) {
        LambdaQueryWrapper<SpecialDeduction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpecialDeduction::getEmployeeId, employeeId)
                   .eq(SpecialDeduction::getYear, year);
        SpecialDeduction deduction = specialDeductionMapper.selectOne(queryWrapper);
        return ResponseResult.success(deduction);
    }

    /**
     * 添加专项附加扣除页面
     */
    @GetMapping("/add")
    public String add() {
        return "deduction/add";
    }

    /**
     * 添加专项附加扣除
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseResult<Void> doAdd(SpecialDeduction specialDeduction) {
        // 参数校验
        if (specialDeduction.getEmployeeId() == null) {
            return ResponseResult.error("员工不能为空");
        }
        if (specialDeduction.getYear() == null) {
            return ResponseResult.error("年份不能为空");
        }

        // 检查员工是否存在
        Employee employee = employeeMapper.selectById(specialDeduction.getEmployeeId());
        if (employee == null) {
            return ResponseResult.error("员工不存在");
        }

        // 检查是否已存在该员工该年份的专项附加扣除
        LambdaQueryWrapper<SpecialDeduction> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SpecialDeduction::getEmployeeId, specialDeduction.getEmployeeId())
                   .eq(SpecialDeduction::getYear, specialDeduction.getYear());
        if (specialDeductionMapper.selectCount(queryWrapper) > 0) {
            return ResponseResult.error("该员工该年份的专项附加扣除已存在");
        }

        // 设置默认值
        if (specialDeduction.getChildrenEducation() == null) {
            specialDeduction.setChildrenEducation(BigDecimal.ZERO);
        }
        if (specialDeduction.getContinuingEducation() == null) {
            specialDeduction.setContinuingEducation(BigDecimal.ZERO);
        }
        if (specialDeduction.getHousingLoan() == null) {
            specialDeduction.setHousingLoan(BigDecimal.ZERO);
        }
        if (specialDeduction.getHousingRent() == null) {
            specialDeduction.setHousingRent(BigDecimal.ZERO);
        }
        if (specialDeduction.getElderlyCare() == null) {
            specialDeduction.setElderlyCare(BigDecimal.ZERO);
        }
        if (specialDeduction.getMedicalExpense() == null) {
            specialDeduction.setMedicalExpense(BigDecimal.ZERO);
        }
        if (specialDeduction.getChildCare() == null) {
            specialDeduction.setChildCare(BigDecimal.ZERO);
        }
        specialDeduction.setCreateTime(new Date());
        specialDeduction.setUpdateTime(new Date());

        // 保存专项附加扣除
        specialDeductionMapper.insert(specialDeduction);

        return ResponseResult.success();
    }

    /**
     * 编辑专项附加扣除页面
     */
    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseResult edit(@PathVariable Integer id, Model model) {
        SpecialDeduction specialDeduction = specialDeductionMapper.selectById(id);
        model.addAttribute("specialDeduction", specialDeduction);

        // 获取员工信息
        if (specialDeduction != null && specialDeduction.getEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(specialDeduction.getEmployeeId());
            model.addAttribute("employee", employee);
        }

        return ResponseResult.success(specialDeduction);
    }

    /**
     * 编辑专项附加扣除
     */
    @PostMapping("/edit")
    @ResponseBody
    public ResponseResult<Void> doEdit(SpecialDeduction specialDeduction) {
        // 参数校验
        if (specialDeduction.getId() == null) {
            return ResponseResult.error("专项附加扣除ID不能为空");
        }

        // 检查专项附加扣除是否存在
        SpecialDeduction existDeduction = specialDeductionMapper.selectById(specialDeduction.getId());
        if (existDeduction == null) {
            return ResponseResult.error("专项附加扣除不存在");
        }

        // 设置不需要修改的字段
        specialDeduction.setEmployeeId(existDeduction.getEmployeeId());
        specialDeduction.setYear(existDeduction.getYear());
        specialDeduction.setCreateTime(existDeduction.getCreateTime());
        specialDeduction.setUpdateTime(new Date());

        // 更新专项附加扣除
        specialDeductionMapper.updateById(specialDeduction);

        return ResponseResult.success();
    }

    /**
     * 删除专项附加扣除
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseResult<Void> delete(@PathVariable Integer id) {
        // 检查专项附加扣除是否存在
        SpecialDeduction specialDeduction = specialDeductionMapper.selectById(id);
        if (specialDeduction == null) {
            return ResponseResult.error("专项附加扣除不存在");
        }

        // 删除专项附加扣除
        specialDeductionMapper.deleteById(id);

        return ResponseResult.success();
    }

    /**
     * 查看专项附加扣除详情
     */
    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseResult detail(@PathVariable Integer id, Model model) {
        SpecialDeduction specialDeduction = specialDeductionMapper.selectById(id);
        model.addAttribute("specialDeduction", specialDeduction);

        // 获取员工信息
        if (specialDeduction != null && specialDeduction.getEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(specialDeduction.getEmployeeId());
            model.addAttribute("employee", employee);
        }
        return ResponseResult.success(specialDeduction);
    }
}
