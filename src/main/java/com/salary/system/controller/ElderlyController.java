package com.salary.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.system.entity.Elderly;
import com.salary.system.entity.Employee;
import com.salary.system.mapper.ElderlyMapper;
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
 * 老人信息控制器
 */
@Controller
@RequestMapping("/elderly")
public class ElderlyController {

    @Resource
    private ElderlyMapper elderlyMapper;

    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 老人信息管理页面
     */
    @GetMapping("")
    public String index() {
        return "elderly/index";
    }

    /**
     * 老人信息列表
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseResult<IPage<Elderly>> list(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              Integer employeeId) {
        LambdaQueryWrapper<Elderly> queryWrapper = new LambdaQueryWrapper<>();
        if (employeeId != null) {
            queryWrapper.eq(Elderly::getEmployeeId, employeeId);
        }

        IPage<Elderly> page = new Page<>(current, size);
        page = elderlyMapper.selectPage(page, queryWrapper);

        // 对身份证号进行脱敏处理
        for (Elderly elderly : page.getRecords()) {
            try {
                if (elderly.getIdCard() != null && !elderly.getIdCard().isEmpty()) {
                    // 先解密，再脱敏
                    String idCard = SM4Util.decrypt(elderly.getIdCard());
                    elderly.setIdCard(MaskUtil.maskIdCard(idCard));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ResponseResult.success(page);
    }

    /**
     * 获取员工的所有老人信息
     */
    @GetMapping("/employee/{employeeId}")
    @ResponseBody
    public ResponseResult<List<Elderly>> getByEmployeeId(@PathVariable Integer employeeId) {
        LambdaQueryWrapper<Elderly> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Elderly::getEmployeeId, employeeId);
        List<Elderly> elderlyList = elderlyMapper.selectList(queryWrapper);

        // 对身份证号进行脱敏处理
        for (Elderly elderly : elderlyList) {
            try {
                if (elderly.getIdCard() != null && !elderly.getIdCard().isEmpty()) {
                    String idCard = SM4Util.decrypt(elderly.getIdCard());
                    elderly.setIdCard(MaskUtil.maskIdCard(idCard));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return ResponseResult.success(elderlyList);
    }

    /**
     * 添加老人信息页面
     */
    @GetMapping("/add")
    public String add() {
        return "elderly/add";
    }

    /**
     * 添加老人信息
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseResult<Void> doAdd(Elderly elderly) {
        // 参数校验
        if (elderly.getEmployeeId() == null) {
            return ResponseResult.error("员工不能为空");
        }
        if (elderly.getName() == null || elderly.getName().trim().isEmpty()) {
            return ResponseResult.error("姓名不能为空");
        }
        if (elderly.getIdCard() == null || elderly.getIdCard().trim().isEmpty()) {
            return ResponseResult.error("身份证号不能为空");
        }
        if (elderly.getRelation() == null || elderly.getRelation().trim().isEmpty()) {
            return ResponseResult.error("关系不能为空");
        }

        // 检查员工是否存在
        Employee employee = employeeMapper.selectById(elderly.getEmployeeId());
        if (employee == null) {
            return ResponseResult.error("员工不存在");
        }

        try {
            // 身份证号加密
            elderly.setIdCard(SM4Util.encrypt(elderly.getIdCard()));

            // 设置默认值
            if (elderly.getIsSoleSupport() == null) {
                elderly.setIsSoleSupport(0);
            }
            elderly.setCreateTime(new Date());
            elderly.setUpdateTime(new Date());

            // 保存老人信息
            elderlyMapper.insert(elderly);

            return ResponseResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("系统错误：" + e.getMessage());
        }
    }

    /**
     * 编辑老人信息页面
     */
    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseResult edit(@PathVariable Integer id, Model model) {
        Elderly elderly = elderlyMapper.selectById(id);

        try {
            // 解密身份证号
            if (elderly.getIdCard() != null && !elderly.getIdCard().isEmpty()) {
                elderly.setIdCard(SM4Util.decrypt(elderly.getIdCard()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("elderly", elderly);

        return ResponseResult.success(elderly);
    }

    /**
     * 编辑老人信息
     */
    @PostMapping("/edit")
    @ResponseBody
    public ResponseResult<Void> doEdit(Elderly elderly) {
        // 参数校验
        if (elderly.getId() == null) {
            return ResponseResult.error("老人ID不能为空");
        }
        if (elderly.getName() == null || elderly.getName().trim().isEmpty()) {
            return ResponseResult.error("姓名不能为空");
        }
        if (elderly.getIdCard() == null || elderly.getIdCard().trim().isEmpty()) {
            return ResponseResult.error("身份证号不能为空");
        }
        if (elderly.getRelation() == null || elderly.getRelation().trim().isEmpty()) {
            return ResponseResult.error("关系不能为空");
        }

        // 检查老人信息是否存在
        Elderly existElderly = elderlyMapper.selectById(elderly.getId());
        if (existElderly == null) {
            return ResponseResult.error("老人信息不存在");
        }

        try {
            // 身份证号加密
            elderly.setIdCard(SM4Util.encrypt(elderly.getIdCard()));

            // 设置不需要修改的字段
            elderly.setEmployeeId(existElderly.getEmployeeId());
            elderly.setCreateTime(existElderly.getCreateTime());
            elderly.setUpdateTime(new Date());

            // 更新老人信息
            elderlyMapper.updateById(elderly);

            return ResponseResult.success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.error("系统错误：" + e.getMessage());
        }
    }

    /**
     * 删除老人信息
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseResult<Void> delete(@PathVariable Integer id) {
        // 检查老人信息是否存在
        Elderly elderly = elderlyMapper.selectById(id);
        if (elderly == null) {
            return ResponseResult.error("老人信息不存在");
        }

        // 删除老人信息
        elderlyMapper.deleteById(id);

        return ResponseResult.success();
    }

    /**
     * 查看老人信息详情
     */
    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseResult detail(@PathVariable Integer id, Model model) {
        Elderly elderly = elderlyMapper.selectById(id);

        try {
            // 解密身份证号并脱敏
            if (elderly.getIdCard() != null && !elderly.getIdCard().isEmpty()) {
                String idCard = SM4Util.decrypt(elderly.getIdCard());
                elderly.setIdCard(MaskUtil.maskIdCard(idCard));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        model.addAttribute("elderly", elderly);

        // 获取员工信息
        if (elderly != null && elderly.getEmployeeId() != null) {
            Employee employee = employeeMapper.selectById(elderly.getEmployeeId());
            model.addAttribute("employee", employee);
        }

        return ResponseResult.success(elderly);
    }
}
