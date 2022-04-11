package com.example.qukuailian.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new AuctionControllerInterceptor())
//                .addPathPatterns("/auction/createkey", "/auction/pkenc", "/auction/decinfo");
//        registry.addInterceptor(new PaperControllerInterceptor())
//                .addPathPatterns("/paper/issue", "/paper/buy", "/paper/check", "/paper/transfer");
    }
}
