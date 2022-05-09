package com.example.qukuailian.interceptor;

import com.example.qukuailian.bean.Message;
import com.example.qukuailian.service.CheckService;
import com.example.qukuailian.util.OPE.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControllerInterceptor implements HandlerInterceptor {

    @Autowired
    CheckService checkService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String username = (String) request.getParameter("username");
        if (checkService.check(username)) {
            return true;
        } else {
            Message<String> ret = MessageUtil.error("Blocked, please authenticate!");
            System.out.println(ret);
            response.getWriter().println(ret);
            return false;
        }
    }
}
