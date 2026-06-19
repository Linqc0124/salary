package com.salary.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.salary.system.entity.AuditLog;
import com.salary.system.entity.User;
import com.salary.system.mapper.AuditLogMapper;
import com.salary.system.mapper.UserMapper;
import com.salary.system.util.ResponseResult;
import com.salary.system.util.SM3Util;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 登录控制器
 */
@Controller
public class LoginController {
    
    @Resource
    private UserMapper userMapper;
    
    @Resource
    private AuditLogMapper auditLogMapper;
    
    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * 登录处理
     */
    @PostMapping("/login")
    @ResponseBody
    public ResponseResult<Void> doLogin(String username, String password, HttpServletRequest request) {
        // 参数校验
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return ResponseResult.error("用户名或密码不能为空");
        }
        
        // 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);
        
        // 记录审计日志
        AuditLog auditLog = new AuditLog();
        auditLog.setUsername(username);
        auditLog.setOperation("登录");
        auditLog.setMethod(request.getMethod() + " " + request.getRequestURI());
        auditLog.setIp(getClientIP(request));
        auditLog.setCreateTime(new Date());
        
        // 用户不存在
        if (user == null) {
            auditLog.setStatus(0);
            auditLog.setMessage("登录失败：用户不存在");
            auditLogMapper.insert(auditLog);
            return ResponseResult.error("用户名或密码错误");
        }
        
        // 账号已禁用
        if (user.getStatus() == 0) {
            auditLog.setStatus(0);
            auditLog.setMessage("登录失败：账号已禁用");
            auditLogMapper.insert(auditLog);
            return ResponseResult.error("账号已被禁用，请联系管理员");
        }
        
        // 检查账号是否锁定
        if (user.getLockedTime() != null) {
            long lockDuration = System.currentTimeMillis() - user.getLockedTime().getTime();
            // 锁定30分钟
            if (lockDuration < 30 * 60 * 1000) {
                auditLog.setStatus(0);
                auditLog.setMessage("登录失败：账号已锁定");
                auditLogMapper.insert(auditLog);
                return ResponseResult.error("账号已锁定，请" + (30 - lockDuration / 60000) + "分钟后再试");
            }
        }
        
        // 密码加密
        String encryptedPassword = SM3Util.encrypt(password);
        
        // 验证密码
        if (!user.getPassword().equals(encryptedPassword)) {
            // 密码错误，增加失败次数
            user.setLoginFailCount(user.getLoginFailCount() == null ? 1 : user.getLoginFailCount() + 1);
            
            // 失败5次锁定账号
            if (user.getLoginFailCount() >= 5) {
                user.setLockedTime(new Date());
                userMapper.updateById(user);
                
                auditLog.setStatus(0);
                auditLog.setMessage("登录失败：密码错误5次，账号已锁定");
                auditLogMapper.insert(auditLog);
                return ResponseResult.error("密码错误5次，账号已锁定30分钟");
            }
            
            userMapper.updateById(user);
            
            auditLog.setStatus(0);
            auditLog.setMessage("登录失败：密码错误");
            auditLogMapper.insert(auditLog);
            return ResponseResult.error("用户名或密码错误，还剩" + (5 - user.getLoginFailCount()) + "次机会");
        }
        
        // 检查密码是否过期（90天）
        Date lastReset = user.getLastPasswordResetDate();
        if (lastReset != null) {
            long daysPassed = (System.currentTimeMillis() - lastReset.getTime()) / (1000 * 60 * 60 * 24);
            if (daysPassed >= 90) {
                auditLog.setStatus(0);
                auditLog.setMessage("登录失败：密码已过期");
                auditLogMapper.insert(auditLog);
                return ResponseResult.error(401, "密码已过期，请重置密码");
            }
        }
        
        // 登录成功，清除失败次数和锁定时间
        user.setLoginFailCount(0);
        user.setLockedTime(null);
        userMapper.updateById(user);
        
        // 设置会话
        HttpSession session = request.getSession();
        session.setAttribute("user", user);
        session.setAttribute("lastAccessTime", new Date());
        
        auditLog.setUserId(user.getId());
        auditLog.setStatus(1);
        auditLog.setMessage("登录成功");
        auditLogMapper.insert(auditLog);
        
        return ResponseResult.success();
    }
    
    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                // 记录退出日志
                AuditLog auditLog = new AuditLog();
                auditLog.setUserId(user.getId());
                auditLog.setUsername(user.getUsername());
                auditLog.setOperation("退出登录");
                auditLog.setMethod(request.getMethod() + " " + request.getRequestURI());
                auditLog.setIp(getClientIP(request));
                auditLog.setCreateTime(new Date());
                auditLog.setStatus(1);
                auditLog.setMessage("退出登录成功");
                auditLogMapper.insert(auditLog);
            }
            session.invalidate();
        }
        return "redirect:/login";
    }
    
    /**
     * 首页
     */
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }
    
    /**
     * 获取客户端IP
     */
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    /**
     * 403页面
     */
    @GetMapping("/403")
    public String forbidden() {
        return "403";
    }
} 