package com.salary.system.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salary.system.entity.AuditLog;
import com.salary.system.entity.User;
import com.salary.system.mapper.AuditLogMapper;
import com.salary.system.util.SM3Util;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 审计日志拦截器
 */
public class AuditLogInterceptor implements HandlerInterceptor {
    
    @Resource
    private AuditLogMapper auditLogMapper;
    
    // 用于排除不需要记录日志的URL
    private static final String[] EXCLUDE_URLS = {"/static", "/login", "/logout", "/favicon.ico"};
    
    // 用于生成HMAC的密钥
    private static final String HMAC_KEY = "AuditLogSecretKey";
    
    // JSON处理
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        
        // 排除不需要记录日志的URL
        for (String excludeUrl : EXCLUDE_URLS) {
            if (uri.startsWith(request.getContextPath() + excludeUrl)) {
                return true;
            }
        }
        
        // 保存请求开始时间，用于计算处理时长
        request.setAttribute("startTime", System.currentTimeMillis());
        
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String uri = request.getRequestURI();
        
        // 排除不需要记录日志的URL
        for (String excludeUrl : EXCLUDE_URLS) {
            if (uri.startsWith(request.getContextPath() + excludeUrl)) {
                return;
            }
        }
        
        // 记录审计日志
        try {
            AuditLog auditLog = new AuditLog();
            HttpSession session = request.getSession(false);
            
            // 设置用户信息
            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null) {
                    auditLog.setUserId(user.getId());
                    auditLog.setUsername(user.getUsername());
                }
            }
            
            // 设置请求信息
            auditLog.setOperation(getOperation(request));
            auditLog.setMethod(request.getMethod() + " " + uri);
            auditLog.setIp(getClientIP(request));
            auditLog.setCreateTime(new Date());
            
            // 设置参数信息
            Map<String, Object> paramMap = new HashMap<>();
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                // 过滤敏感参数
                if (!"password".equals(paramName)) {
                    paramMap.put(paramName, request.getParameter(paramName));
                }
            }
            auditLog.setParams(objectMapper.writeValueAsString(paramMap));
            
            // 设置操作状态
            auditLog.setStatus(ex == null ? 1 : 0);
            auditLog.setMessage(ex != null ? ex.getMessage() : "操作成功");
            
            // 计算HMAC值
            String logData = auditLog.getUserId() + ":" + auditLog.getUsername() + ":" + 
                            auditLog.getOperation() + ":" + auditLog.getMethod() + ":" +
                            auditLog.getParams() + ":" + auditLog.getIp() + ":" +
                            auditLog.getCreateTime() + ":" + auditLog.getStatus() + ":" +
                            auditLog.getMessage();
            auditLog.setHmac(SM3Util.hmac(HMAC_KEY, logData));
            
            // 保存到数据库
            if (auditLogMapper != null) {
                auditLogMapper.insert(auditLog);
            }
            
        } catch (Exception e) {
            // 记录日志失败不影响正常业务
            e.printStackTrace();
        }
    }
    
    /**
     * 获取操作类型
     */
    private String getOperation(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        
        if ("GET".equals(method)) {
            if (uri.contains("/list") || uri.contains("/query")) {
                return "查询";
            } else if (uri.contains("/detail")) {
                return "查看详情";
            } else {
                return "访问页面";
            }
        } else if ("POST".equals(method)) {
            if (uri.contains("/add") || uri.contains("/save")) {
                return "新增";
            } else if (uri.contains("/update") || uri.contains("/edit")) {
                return "修改";
            } else if (uri.contains("/delete") || uri.contains("/remove")) {
                return "删除";
            } else if (uri.contains("/import")) {
                return "导入";
            } else if (uri.contains("/export")) {
                return "导出";
            } else if (uri.contains("/upload")) {
                return "上传";
            } else if (uri.contains("/download")) {
                return "下载";
            } else if (uri.contains("/login")) {
                return "登录";
            } else if (uri.contains("/logout")) {
                return "登出";
            } else {
                return "其他操作";
            }
        }
        
        return "未知操作";
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
} 