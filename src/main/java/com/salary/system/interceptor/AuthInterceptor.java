package com.salary.system.interceptor;

import com.salary.system.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限拦截器
 */
public class AuthInterceptor implements HandlerInterceptor {

    // 权限配置：URL路径 -> 允许访问的角色列表
    private static final Map<String, List<String>> URL_ROLE_MAPPING = new HashMap<>();

    static {
        // 系统管理员可以访问所有页面
        // 用户管理
        URL_ROLE_MAPPING.put("/user", Arrays.asList("ADMIN"));
        URL_ROLE_MAPPING.put("/user/list", Arrays.asList("ADMIN"));
        URL_ROLE_MAPPING.put("/user/add", Arrays.asList("ADMIN"));
        URL_ROLE_MAPPING.put("/user/edit", Arrays.asList("ADMIN"));
        URL_ROLE_MAPPING.put("/user/delete", Arrays.asList("ADMIN"));
        
        // 部门管理
        URL_ROLE_MAPPING.put("/department", Arrays.asList("ADMIN", "HR"));
        URL_ROLE_MAPPING.put("/department/list", Arrays.asList("ADMIN", "HR", "MANAGER"));
        URL_ROLE_MAPPING.put("/department/add", Arrays.asList("ADMIN", "HR"));
        URL_ROLE_MAPPING.put("/department/edit", Arrays.asList("ADMIN", "HR"));
        URL_ROLE_MAPPING.put("/department/delete", Arrays.asList("ADMIN", "HR"));
        
        // 员工管理
        URL_ROLE_MAPPING.put("/employee", Arrays.asList("ADMIN", "HR", "MANAGER"));
        URL_ROLE_MAPPING.put("/employee/list", Arrays.asList("ADMIN", "HR", "MANAGER"));
        URL_ROLE_MAPPING.put("/employee/add", Arrays.asList("ADMIN", "HR"));
        URL_ROLE_MAPPING.put("/employee/edit", Arrays.asList("ADMIN", "HR"));
        URL_ROLE_MAPPING.put("/employee/delete", Arrays.asList("ADMIN", "HR"));

        // 子女和老人信息管理
        URL_ROLE_MAPPING.put("/child", Arrays.asList("ADMIN", "HR"));
        URL_ROLE_MAPPING.put("/child/list", Arrays.asList("ADMIN", "HR", "MANAGER"));
        URL_ROLE_MAPPING.put("/elderly", Arrays.asList("ADMIN", "HR"));
        URL_ROLE_MAPPING.put("/elderly/list", Arrays.asList("ADMIN", "HR", "MANAGER"));
        
        // 工资配置和管理
        URL_ROLE_MAPPING.put("/salary/config", Arrays.asList("ADMIN", "FINANCE"));
        URL_ROLE_MAPPING.put("/salary/record", Arrays.asList("ADMIN", "FINANCE"));
        URL_ROLE_MAPPING.put("/salary/import", Arrays.asList("ADMIN", "FINANCE"));
        URL_ROLE_MAPPING.put("/salary/history", Arrays.asList("ADMIN", "FINANCE", "MANAGER"));

        // 专项附加扣除管理
        URL_ROLE_MAPPING.put("/deduction", Arrays.asList("ADMIN", "FINANCE"));
        URL_ROLE_MAPPING.put("/deduction/list", Arrays.asList("ADMIN", "FINANCE", "MANAGER"));
        
        // 审计日志
        URL_ROLE_MAPPING.put("/audit/log", Arrays.asList("ADMIN", "AUDIT"));
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // 未登录的情况已被LoginInterceptor处理，此处不再重复处理
        if (user == null) {
            return true;
        }
        
        String requestURI = request.getRequestURI().replace(request.getContextPath(), "");
        
        // 系统管理员有所有权限
        if ("ADMIN".equals(user.getRole())) {
            return true;
        }
        
        // 判断当前路径是否需要进行权限控制
        for (String url : URL_ROLE_MAPPING.keySet()) {
            if (requestURI.startsWith(url)) {
                List<String> roles = URL_ROLE_MAPPING.get(url);
                if (roles.contains(user.getRole())) {
                    return true;
                } else {
                    // 无权限访问，跳转到403页面
                    response.sendRedirect(request.getContextPath() + "/403");
                    return false;
                }
            }
        }
        
        // 未配置权限的路径默认放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
} 