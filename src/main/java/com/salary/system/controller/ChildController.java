package com.salary.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.system.entity.Child;
import com.salary.system.entity.Employee;
import com.salary.system.mapper.ChildMapper;
import com.salary.system.mapper.EmployeeMapper;
import com.salary.system.util.MaskUtil;
import com.salary.system.util.ResponseResult;
import com.salary.system.util.SM4Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 子女信息控制器
 */
@Controller
@RequestMapping("/child")
public class ChildController {

    @Resource
    private ChildMapper childMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 子女信息管理页面
     */
    @GetMapping("")
    public String index() {
        return "child/index";
    }

    /**
     * 子女信息列表
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseResult<IPage<Child>> list(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            Integer employeeId) {
        LambdaQueryWrapper<Child> queryWrapper = new LambdaQueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq(Child::getEmployeeId, employeeId);
        }

        IPage<Child> page = new Page<>(current, size);
        page = childMapper.selectPage(page, queryWrapper);

        // 对身份证号进行脱敏处理
        for (Child child : page.getRecords()) {
            try {
                if (child.getIdCard() != null && !child.getIdCard().isEmpty()) {
                    // 先解密，再脱敏
                    String idCard = SM4Util.decrypt(child.getIdCard());
                    child.setIdCard(MaskUtil.maskIdCard(idCard));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ResponseResult.success(page);
    }

    /**
     * 获取员工的所有子女信息
     */
    @GetMapping("/employee/{employeeId}")
    @ResponseBody
    public ResponseResult<List<Child>> getByEmployeeId(@PathVariable Integer employeeId) {
        LambdaQueryWrapper<Child> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Child::getEmployeeId, employeeId);
        List<Child> children = childMapper.selectList(queryWrapper);

        // 对身份证号进行脱敏处理
        for (Child child : children) {
            try {
                if (child.getIdCard() != null && !child.getIdCard().isEmpty()) {
                    String idCard = SM4Util.decrypt(child.getIdCard());
                    child.setIdCard(MaskUtil.maskIdCard(idCard));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ResponseResult.success(children);
    }

    /**
     * 添加子女信息页面
     */
    @GetMapping("/add")
    public String add() {
        return "child/add";
    }

    /**
     * 添加子女信息
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseResult<Void> doAdd(Child child) {
        // 参数校验
        if (child.getEmployeeId() == null) {
            return ResponseResult.error("员工不能为空");
        }
        if (child.getName() == null || child.getName().trim().isEmpty()) {
            return ResponseResult.error("姓名不能为空");
        }
        if (child.getIdCard() == null || child.getIdCard().trim().isEmpty()) {
            return ResponseResult.error("身份证号不能为空");
        }
        if (child.getRelation() == null || child.getRelation().trim().isEmpty()) {
            return ResponseResult.error("关系不能为空");
        }
        if (child.getEducationStage() == null || child.getEducationStage().trim().isEmpty()) {
            return ResponseResult.error("教育阶段不能为空");
        }

        // 检查员工是否存在
        Employee employee = employeeMapper.selectById(child.getEmployeeId());
        if (employee == null) {
            return ResponseResult.error("员工不存在");
        }

        try {
            // 身份证号加密
            child.setIdCard(SM4Util.encrypt(child.getIdCard()));

            // 设置默认值
            child.setCreateTime(new Date());
            child.setUpdateTime(new Date());

            // 保存子女信息
            childMapper.insert(child);

            return ResponseResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("系统错误：" + e.getMessage());
        }
    }

    /**
     * 编辑子女信息页面
     */
    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseResult edit(@PathVariable Integer id, Model model) {
        Child child = childMapper.selectById(id);

        try {
            // 解密身份证号
            if (child.getIdCard() != null && !child.getIdCard().isEmpty()) {
                child.setIdCard(SM4Util.decrypt(child.getIdCard()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("child", child);

        return ResponseResult.success(child);
    }

    /**
     * 编辑子女信息
     */
    @PostMapping("/edit")
    @ResponseBody
    public ResponseResult<Void> doEdit(Child child) {
        // 参数校验
        if (child.getId() == null) {
            return ResponseResult.error("子女ID不能为空");
        }
        if (child.getName() == null || child.getName().trim().isEmpty()) {
            return ResponseResult.error("姓名不能为空");
        }
        if (child.getIdCard() == null || child.getIdCard().trim().isEmpty()) {
            return ResponseResult.error("身份证号不能为空");
        }
        if (child.getRelation() == null || child.getRelation().trim().isEmpty()) {
            return ResponseResult.error("关系不能为空");
        }
        if (child.getEducationStage() == null || child.getEducationStage().trim().isEmpty()) {
            return ResponseResult.error("教育阶段不能为空");
        }

        // 检查子女信息是否存在
        Child existChild = childMapper.selectById(child.getId());
        if (existChild == null) {
            return ResponseResult.error("子女信息不存在");
        }

        try {
            // 身份证号加密
            child.setIdCard(SM4Util.encrypt(child.getIdCard()));

            // 设置不需要修改的字段
            child.setEmployeeId(existChild.getEmployeeId());
            child.setCreateTime(existChild.getCreateTime());
            child.setUpdateTime(new Date());

            // 更新子女信息
            childMapper.updateById(child);

            return ResponseResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("系统错误：" + e.getMessage());
        }
    }

    /**
     * 删除子女信息
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseResult<Void> delete(@PathVariable Integer id) {
        // 检查子女信息是否存在
        Child child = childMapper.selectById(id);
        if (child == null) {
            return ResponseResult.error("子女信息不存在");
        }

        // 删除子女信息
        childMapper.deleteById(id);

        return ResponseResult.success();
    }

    /**
     * 查看子女信息详情
     */
    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseResult detail(@PathVariable Integer id, Model model) {
        Child child = childMapper.selectById(id);

        try {
            // 解密身份证号并脱敏
            if (child.getIdCard() != null && !child.getIdCard().isEmpty()) {
                String idCard = SM4Util.decrypt(child.getIdCard());
                child.setIdCard(MaskUtil.maskIdCard(idCard));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("child", child);

        // 获取员工信息
        if (child != null && child.getEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(child.getEmployeeId());
            model.addAttribute("employee", employee);
        }

        return ResponseResult.success(child);
    }
}
