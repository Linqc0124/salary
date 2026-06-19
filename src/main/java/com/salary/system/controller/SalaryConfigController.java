package com.salary.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.system.entity.SalaryConfig;
import com.salary.system.entity.Employee;
import com.salary.system.mapper.SalaryConfigMapper;
import com.salary.system.mapper.EmployeeMapper;
import com.salary.system.util.ResponseResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 工资配置控制器
 */
@Controller
@RequestMapping("/salary/config")
public class SalaryConfigController {

    @Resource
    private SalaryConfigMapper salaryConfigMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 工资配置页面
     */
    @GetMapping("")
    public String index() {
        return "salary/config/index";
    }

    /**
     * 工资配置列表
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseResult<IPage<SalaryConfig>> list(@RequestParam(defaultValue = "1") Integer current,
                                                  @RequestParam(defaultValue = "10") Integer size,
                                                  Integer employeeId) {
        LambdaQueryWrapper<SalaryConfig> queryWrapper = new LambdaQueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq(SalaryConfig::getEmployeeId, employeeId);
        }

        IPage<SalaryConfig> page = new Page<>(current, size);
        page = salaryConfigMapper.selectPage(page, queryWrapper);

        return ResponseResult.success(page);
    }

    /**
     * 添加工资配置页面
     */
    @GetMapping("/add")
    public String add() {
        return "salary/config/add";
    }

    /**
     * 添加工资配置
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseResult<Void> doAdd(SalaryConfig salaryConfig) {
        // 参数校验
        if (salaryConfig.getEmployeeId() == null) {
            return ResponseResult.error("员工不能为空");
        }
        if (salaryConfig.getBaseSalary() == null || salaryConfig.getBaseSalary().compareTo(BigDecimal.ZERO) < 0) {
            return ResponseResult.error("基本工资不能为空且不能小于0");
        }
        if (salaryConfig.getPositionSalary() == null || salaryConfig.getPositionSalary().compareTo(BigDecimal.ZERO) < 0) {
            return ResponseResult.error("岗位工资不能为空且不能小于0");
        }

        // 检查员工是否存在
        Employee employee = employeeMapper.selectById(salaryConfig.getEmployeeId());
        if (employee == null) {
            return ResponseResult.error("员工不存在");
        }

        // 检查员工是否已有工资配置
        LambdaQueryWrapper<SalaryConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SalaryConfig::getEmployeeId, salaryConfig.getEmployeeId())
                   .eq(SalaryConfig::getStatus, 1);
        if (salaryConfigMapper.selectCount(queryWrapper) > 0) {
            return ResponseResult.error("该员工已有工资配置，请先禁用原配置");
        }

        // 设置默认值
        salaryConfig.setStatus(1);
        salaryConfig.setCreateTime(new Date());
        salaryConfig.setUpdateTime(new Date());

        // 保存工资配置
        salaryConfigMapper.insert(salaryConfig);

        return ResponseResult.success();
    }

    /**
     * 编辑工资配置页面
     */
    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseResult edit(@PathVariable Integer id, Model model) {
        SalaryConfig salaryConfig = salaryConfigMapper.selectById(id);
        model.addAttribute("salaryConfig", salaryConfig);

        // 获取员工信息
        if (salaryConfig != null && salaryConfig.getEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(salaryConfig.getEmployeeId());
            model.addAttribute("employee", employee);
        }

        return ResponseResult.success(salaryConfig);
    }

    /**
     * 编辑工资配置
     */
    @PostMapping("/edit")
    @ResponseBody
    public ResponseResult<Void> doEdit(SalaryConfig salaryConfig) {
        // 参数校验
        if (salaryConfig.getId() == null) {
            return ResponseResult.error("工资配置ID不能为空");
        }
        if (salaryConfig.getBaseSalary() == null || salaryConfig.getBaseSalary().compareTo(BigDecimal.ZERO) < 0) {
            return ResponseResult.error("基本工资不能为空且不能小于0");
        }
        if (salaryConfig.getPositionSalary() == null || salaryConfig.getPositionSalary().compareTo(BigDecimal.ZERO) < 0) {
            return ResponseResult.error("岗位工资不能为空且不能小于0");
        }

        // 检查工资配置是否存在
        SalaryConfig existConfig = salaryConfigMapper.selectById(salaryConfig.getId());
        if (existConfig == null) {
            return ResponseResult.error("工资配置不存在");
        }

        // 设置不需要修改的字段
        salaryConfig.setEmployeeId(existConfig.getEmployeeId());
        salaryConfig.setCreateTime(existConfig.getCreateTime());
        salaryConfig.setUpdateTime(new Date());

        // 更新工资配置
        salaryConfigMapper.updateById(salaryConfig);

        return ResponseResult.success();
    }

    /**
     * 启用/禁用工资配置
     */
    @PostMapping("/toggle/{id}")
    @ResponseBody
    public ResponseResult<Void> toggle(@PathVariable Integer id) {
        // 检查工资配置是否存在
        SalaryConfig salaryConfig = salaryConfigMapper.selectById(id);
        if (salaryConfig == null) {
            return ResponseResult.error("工资配置不存在");
        }

        // 切换状态
        salaryConfig.setStatus(salaryConfig.getStatus() == 1 ? 0 : 1);
        salaryConfig.setUpdateTime(new Date());

        // 更新工资配置
        salaryConfigMapper.updateById(salaryConfig);

        return ResponseResult.success();
    }

    /**
     * 查看工资配置详情
     */
    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseResult detail(@PathVariable Integer id, Model model) {
        SalaryConfig salaryConfig = salaryConfigMapper.selectById(id);
        model.addAttribute("salaryConfig", salaryConfig);

        // 获取员工信息
        if (salaryConfig != null && salaryConfig.getEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(salaryConfig.getEmployeeId());
            model.addAttribute("employee", employee);
        }

        return ResponseResult.success(salaryConfig);
    }
}
