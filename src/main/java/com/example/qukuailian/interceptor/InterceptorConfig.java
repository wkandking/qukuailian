package com.example.qukuailian.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ControllerInterceptor())
                .addPathPatterns("/encrypted/auction/createkey", "/encrypted/auction/pkenc", "/encrypted/auction/decinfo")
                .addPathPatterns("/encrypted/paper/issue", "/encrypted/paper/buy", "/encrypted/paper/check", "/encrypted/paper/transfer");
    }
}
