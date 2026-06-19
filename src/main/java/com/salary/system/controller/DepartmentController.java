package com.salary.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.system.entity.Department;
import com.salary.system.entity.Employee;
import com.salary.system.mapper.DepartmentMapper;
import com.salary.system.mapper.EmployeeMapper;
import com.salary.system.util.ResponseResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 部门管理控制器
 */
@Controller
@RequestMapping("/department")
public class DepartmentController {

    @Resource
    private DepartmentMapper departmentMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 部门管理页面
     */
    @GetMapping("")
    public String index() {
        return "department/index";
    }

    /**
     * 部门列表
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseResult<IPage<Department>> list(@RequestParam(defaultValue = "1") Integer current,
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 String name, String code) {
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(Department::getName, name);
        }
        if (code != null && !code.trim().isEmpty()) {
            queryWrapper.like(Department::getCode, code);
        }

        IPage<Department> page = new Page<>(current, size);
        page = departmentMapper.selectPage(page, queryWrapper);

        return ResponseResult.success(page);
    }

    /**
     * 获取所有部门（不分页）
     */
    @GetMapping("/all")
    @ResponseBody
    public ResponseResult<List<Department>> all() {
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Department::getStatus, 1); // 只查询启用的部门
        List<Department> departments = departmentMapper.selectList(queryWrapper);
        return ResponseResult.success(departments);
    }

    /**
     * 添加部门页面
     */
    @GetMapping("/add")
    public String add() {
        return "department/add";
    }

    /**
     * 添加部门
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseResult<Void> doAdd(Department department) {
        // 参数校验
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            return ResponseResult.error("部门名称不能为空");
        }
        if (department.getCode() == null || department.getCode().trim().isEmpty()) {
            return ResponseResult.error("部门代码不能为空");
        }

        // 检查部门代码是否已存在
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Department::getCode, department.getCode());
        if (departmentMapper.selectCount(queryWrapper) > 0) {
            return ResponseResult.error("部门代码已存在");
        }

        // 设置默认值
        department.setStatus(1);
        department.setCreateTime(new Date());
        department.setUpdateTime(new Date());

        // 保存部门
        departmentMapper.insert(department);

        return ResponseResult.success();
    }

    /**
     * 编辑部门页面
     */
    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseResult edit(@PathVariable Integer id, Model model) {
        Department department = departmentMapper.selectById(id);
        model.addAttribute("department", department);
        return ResponseResult.success(department);
    }

    /**
     * 编辑部门
     */
    @PostMapping("/edit")
    @ResponseBody
    public ResponseResult<Void> doEdit(Department department) {
        // 参数校验
        if (department.getId() == null) {
            return ResponseResult.error("部门ID不能为空");
        }
        if (department.getName() == null || department.getName().trim().isEmpty()) {
            return ResponseResult.error("部门名称不能为空");
        }
        if (department.getCode() == null || department.getCode().trim().isEmpty()) {
            return ResponseResult.error("部门代码不能为空");
        }

        // 检查部门是否存在
        Department existDepartment = departmentMapper.selectById(department.getId());
        if (existDepartment == null) {
            return ResponseResult.error("部门不存在");
        }

        // 检查部门代码是否已被其他部门使用
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Department::getCode, department.getCode())
                   .ne(Department::getId, department.getId());
        if (departmentMapper.selectCount(queryWrapper) > 0) {
            return ResponseResult.error("部门代码已存在");
        }

        // 设置不需要修改的字段
        department.setCreateTime(existDepartment.getCreateTime());
        department.setUpdateTime(new Date());

        // 更新部门
        departmentMapper.updateById(department);

        return ResponseResult.success();
    }

    /**
     * 删除部门
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseResult<Void> delete(@PathVariable Integer id) {
        // 检查部门是否存在
        Department department = departmentMapper.selectById(id);
        if (department == null) {
            return ResponseResult.error("部门不存在");
        }

        // 检查部门是否有员工
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getDepartmentId, id);
        if (employeeMapper.selectCount(queryWrapper) > 0) {
            return ResponseResult.error("部门下存在员工，不能删除");
        }

        // 删除部门
        departmentMapper.deleteById(id);

        return ResponseResult.success();
    }
}
