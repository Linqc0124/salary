package com.salary.system.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;

/**
 * 会话超时拦截器
 */
public class SessionTimeoutInterceptor implements HandlerInterceptor {
    
    // 会话超时时间：30分钟（毫秒）
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            // 获取最后访问时间
            Date lastAccessTime = (Date) session.getAttribute("lastAccessTime");
            
            if (lastAccessTime != null) {
                long currentTime = System.currentTimeMillis();
                // 判断是否超时
                if (currentTime - lastAccessTime.getTime() > SESSION_TIMEOUT) {
                    // 超时，清除会话
                    session.invalidate();
                    // 重定向到登录页
                    response.sendRedirect(request.getContextPath() + "/login?timeout=true");
                    return false;
                }
                
                // 未超时，更新最后访问时间
                session.setAttribute("lastAccessTime", new Date());
            }
        }
        
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
} 