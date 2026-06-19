package com.salary.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.system.entity.AuditLog;
import com.salary.system.mapper.AuditLogMapper;
import com.salary.system.util.ResponseResult;
import com.salary.system.util.SM3Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 审计日志控制器
 */
@Controller
@RequestMapping("/audit/log")
public class AuditLogController {

    @Resource
    private AuditLogMapper auditLogMapper;

    // 用于验证HMAC的密钥
    private static final String HMAC_KEY = "AuditLogSecretKey";

    /**
     * 审计日志管理页面
     */
    @GetMapping("")
    public String index() {
        return "audit/log/index";
    }

    /**
     * 审计日志列表
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseResult<IPage<AuditLog>> list(@RequestParam(defaultValue = "1") Integer current,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               String username, String operation, String ip,
                                               String startTime, String endTime) {
        LambdaQueryWrapper<AuditLog> queryWrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.trim().isEmpty()) {
            queryWrapper.like(AuditLog::getUsername, username);
        }
        if (operation != null && !operation.trim().isEmpty()) {
            queryWrapper.like(AuditLog::getOperation, operation);
        }
        if (ip != null && !ip.trim().isEmpty()) {
            queryWrapper.like(AuditLog::getIp, ip);
        }

        // 按时间范围查询
        if (startTime != null && !startTime.trim().isEmpty()) {
            queryWrapper.ge(AuditLog::getCreateTime, startTime);
        }
        if (endTime != null && !endTime.trim().isEmpty()) {
            queryWrapper.le(AuditLog::getCreateTime, endTime);
        }

        // 按时间倒序排序
        queryWrapper.orderByDesc(AuditLog::getCreateTime);

        IPage<AuditLog> page = new Page<>(current, size);
        page = auditLogMapper.selectPage(page, queryWrapper);

        // 验证日志完整性
        for (AuditLog log : page.getRecords()) {
            // 构建日志数据
            String logData = log.getUserId() + ":" + log.getUsername() + ":" +
                           log.getOperation() + ":" + log.getMethod() + ":" +
                           log.getParams() + ":" + log.getIp() + ":" +
                           log.getCreateTime() + ":" + log.getStatus() + ":" +
                           log.getMessage();

            // 验证HMAC
            boolean isValid = SM3Util.verifyHmac(HMAC_KEY, logData, log.getHmac());
            log.setMessage(isValid ? log.getMessage() : log.getMessage() + " [日志被篡改]");
        }

        return ResponseResult.success(page);
    }

    /**
     * 查看审计日志详情
     */
    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseResult detail(@PathVariable Integer id, Model model) {
        AuditLog auditLog = auditLogMapper.selectById(id);

        if (auditLog != null) {
            // 构建日志数据
            String logData = auditLog.getUserId() + ":" + auditLog.getUsername() + ":" +
                           auditLog.getOperation() + ":" + auditLog.getMethod() + ":" +
                           auditLog.getParams() + ":" + auditLog.getIp() + ":" +
                           auditLog.getCreateTime() + ":" + auditLog.getStatus() + ":" +
                           auditLog.getMessage();

            // 验证HMAC
            boolean isValid = SM3Util.verifyHmac(HMAC_KEY, logData, auditLog.getHmac());
            auditLog.setMessage(isValid ? auditLog.getMessage() : auditLog.getMessage() + " [日志被篡改]");
        }

        model.addAttribute("auditLog", auditLog);

        return ResponseResult.success(auditLog);
    }

    /**
     * 导出审计日志
     */
    @GetMapping("/export")
    @ResponseBody
    public ResponseResult<String> export(String username, String operation, String ip,
                                        String startTime, String endTime) {
        // 实际项目中应实现导出功能，这里仅返回提示信息
        return ResponseResult.success("导出成功");
    }
}
