package com.salary.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.system.entity.Employee;
import com.salary.system.mapper.EmployeeMapper;
import com.salary.system.util.MaskUtil;
import com.salary.system.util.ResponseResult;
import com.salary.system.util.SM4Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 员工管理控制器
 */
@Controller
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 员工管理页面
     */
    @GetMapping("")
    public String index() {
        return "employee/index";
    }

    /**
     * 员工列表
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseResult<IPage<Employee>> list(@RequestParam(defaultValue = "1") Integer current,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               String name, String employeeNo, Integer departmentId) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(Employee::getName, name);
        }
        if (employeeNo != null && !employeeNo.trim().isEmpty()) {
            queryWrapper.like(Employee::getEmployeeNo, employeeNo);
        }
        if (departmentId != null) {
            queryWrapper.eq(Employee::getDepartmentId, departmentId);
        }

        IPage<Employee> page = new Page<>(current, size);
        page = employeeMapper.selectPage(page, queryWrapper);

        // 对敏感数据进行脱敏处理
        for (Employee employee : page.getRecords()) {
            try {
                if (employee.getIdCard() != null && !employee.getIdCard().isEmpty()) {
                    // 先解密，再脱敏
                    String idCard = SM4Util.decrypt(employee.getIdCard());
                    employee.setIdCard(MaskUtil.maskIdCard(idCard));
                }
                if (employee.getPhone() != null && !employee.getPhone().isEmpty()) {
                    String phone = SM4Util.decrypt(employee.getPhone());
                    employee.setPhone(MaskUtil.maskPhone(phone));
                }
                if (employee.getAddress() != null && !employee.getAddress().isEmpty()) {
                    String address = SM4Util.decrypt(employee.getAddress());
                    employee.setAddress(MaskUtil.maskAddress(address));
                }
            } catch (Exception e) {
            }
        }

        return ResponseResult.success(page);
    }

    /**
     * 获取所有员工（不分页）
     */
    @GetMapping("/all")
    @ResponseBody
    public ResponseResult<List<Employee>> all() {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getStatus, 1); // 只查询在职的员工
        List<Employee> employees = employeeMapper.selectList(queryWrapper);

        // 对敏感数据进行脱敏处理
        for (Employee employee : employees) {
            try {
                if (employee.getIdCard() != null && !employee.getIdCard().isEmpty()) {
                    String idCard = SM4Util.decrypt(employee.getIdCard());
                    employee.setIdCard(MaskUtil.maskIdCard(idCard));
                }
                if (employee.getPhone() != null && !employee.getPhone().isEmpty()) {
                    String phone = SM4Util.decrypt(employee.getPhone());
                    employee.setPhone(MaskUtil.maskPhone(phone));
                }
                if (employee.getAddress() != null && !employee.getAddress().isEmpty()) {
                    String address = SM4Util.decrypt(employee.getAddress());
                    employee.setAddress(MaskUtil.maskAddress(address));
                }
            } catch (Exception e) {
            }
        }

        return ResponseResult.success(employees);
    }

    /**
     * 添加员工页面
     */
    @GetMapping("/add")
    public String add() {
        return "employee/add";
    }

    /**
     * 添加员工
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseResult<Void> doAdd(Employee employee, String entryDateStr) {
        // 参数校验
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            return ResponseResult.error("员工姓名不能为空");
        }
        if (employee.getEmployeeNo() == null || employee.getEmployeeNo().trim().isEmpty()) {
            return ResponseResult.error("员工编号不能为空");
        }
        if (employee.getIdCard() == null || employee.getIdCard().trim().isEmpty()) {
            return ResponseResult.error("身份证号不能为空");
        }
        if (employee.getPhone() == null || employee.getPhone().trim().isEmpty()) {
            return ResponseResult.error("手机号不能为空");
        }
        if (employee.getDepartmentId() == null) {
            return ResponseResult.error("所属部门不能为空");
        }
        if (entryDateStr == null || entryDateStr.trim().isEmpty()) {
            return ResponseResult.error("入职日期不能为空");
        }

        // 检查员工编号是否已存在
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getEmployeeNo, employee.getEmployeeNo());
        if (employeeMapper.selectCount(queryWrapper) > 0) {
            return ResponseResult.error("员工编号已存在");
        }

        try {
            // 敏感信息加密
            employee.setIdCard(SM4Util.encrypt(employee.getIdCard()));
            employee.setPhone(SM4Util.encrypt(employee.getPhone()));
            employee.setAddress(SM4Util.encrypt(employee.getAddress()));

            // 处理入职日期
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date entryDate = dateFormat.parse(entryDateStr);
            employee.setEntryDate(entryDate);

            // 设置默认值
            employee.setStatus(1);
            employee.setCreateTime(new Date());
            employee.setUpdateTime(new Date());

            // 保存员工
            employeeMapper.insert(employee);

            return ResponseResult.success();
        } catch (ParseException e) {
            return ResponseResult.error("入职日期格式错误");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("系统错误：" + e.getMessage());
        }
    }

    /**
     * 编辑员工页面
     */
    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseResult edit(@PathVariable Integer id, Model model) {
        Employee employee = employeeMapper.selectById(id);

        try {
            // 解密敏感信息
            if (employee.getIdCard() != null && !employee.getIdCard().isEmpty()) {
                employee.setIdCard(SM4Util.decrypt(employee.getIdCard()));
            }
            if (employee.getPhone() != null && !employee.getPhone().isEmpty()) {
                employee.setPhone(SM4Util.decrypt(employee.getPhone()));
            }
            if (employee.getAddress() != null && !employee.getAddress().isEmpty()) {
                employee.setAddress(SM4Util.decrypt(employee.getAddress()));
            }
        } catch (Exception e) {
        }

        model.addAttribute("employee", employee);

        // 格式化入职日期
        if (employee.getEntryDate() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            model.addAttribute("entryDateStr", dateFormat.format(employee.getEntryDate()));
        }

        return ResponseResult.success(employee);
    }

    /**
     * 编辑员工
     */
    @PostMapping("/edit")
    @ResponseBody
    public ResponseResult<Void> doEdit(Employee employee, String entryDateStr) {
        // 参数校验
        if (employee.getId() == null) {
            return ResponseResult.error("员工ID不能为空");
        }
        if (employee.getName() == null || employee.getName().trim().isEmpty()) {
            return ResponseResult.error("员工姓名不能为空");
        }
        if (employee.getEmployeeNo() == null || employee.getEmployeeNo().trim().isEmpty()) {
            return ResponseResult.error("员工编号不能为空");
        }
        if (employee.getIdCard() == null || employee.getIdCard().trim().isEmpty()) {
            return ResponseResult.error("身份证号不能为空");
        }
        if (employee.getPhone() == null || employee.getPhone().trim().isEmpty()) {
            return ResponseResult.error("手机号不能为空");
        }
        if (employee.getDepartmentId() == null) {
            return ResponseResult.error("所属部门不能为空");
        }
        if (entryDateStr == null || entryDateStr.trim().isEmpty()) {
            return ResponseResult.error("入职日期不能为空");
        }

        // 检查员工是否存在
        Employee existEmployee = employeeMapper.selectById(employee.getId());
        if (existEmployee == null) {
            return ResponseResult.error("员工不存在");
        }

        // 检查员工编号是否已被其他员工使用
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getEmployeeNo, employee.getEmployeeNo())
                   .ne(Employee::getId, employee.getId());
        if (employeeMapper.selectCount(queryWrapper) > 0) {
            return ResponseResult.error("员工编号已存在");
        }

        try {
            // 敏感信息加密
            employee.setIdCard(SM4Util.encrypt(employee.getIdCard()));
            employee.setPhone(SM4Util.encrypt(employee.getPhone()));
            employee.setAddress(SM4Util.encrypt(employee.getAddress()));

            // 处理入职日期
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date entryDate = dateFormat.parse(entryDateStr);
            employee.setEntryDate(entryDate);

            // 设置不需要修改的字段
            employee.setCreateTime(existEmployee.getCreateTime());
            employee.setUpdateTime(new Date());

            // 更新员工
            employeeMapper.updateById(employee);

            return ResponseResult.success();
        } catch (ParseException e) {
            return ResponseResult.error("入职日期格式错误");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("系统错误：" + e.getMessage());
        }
    }

    /**
     * 删除员工
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseResult<Void> delete(@PathVariable Integer id) {
        // 检查员工是否存在
        Employee employee = employeeMapper.selectById(id);
        if (employee == null) {
            return ResponseResult.error("员工不存在");
        }

        // 删除员工（软删除，将状态设置为离职）
        employee.setStatus(0);
        employee.setUpdateTime(new Date());
        employeeMapper.updateById(employee);

        return ResponseResult.success();
    }

    /**
     * 查看员工详情
     */
    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseResult detail(@PathVariable Integer id, Model model) {
        Employee employee = employeeMapper.selectById(id);

        try {
            // 解密敏感信息并脱敏
            if (employee.getIdCard() != null && !employee.getIdCard().isEmpty()) {
                String idCard = SM4Util.decrypt(employee.getIdCard());
                employee.setIdCard(MaskUtil.maskIdCard(idCard));
            }
            if (employee.getPhone() != null && !employee.getPhone().isEmpty()) {
                String phone = SM4Util.decrypt(employee.getPhone());
                employee.setPhone(MaskUtil.maskPhone(phone));
            }
            if (employee.getAddress() != null && !employee.getAddress().isEmpty()) {
                String address = SM4Util.decrypt(employee.getAddress());
                employee.setAddress(MaskUtil.maskAddress(address));
            }
        } catch (Exception e) {
        }

        model.addAttribute("employee", employee);
        return ResponseResult.success(employee);
    }
}
