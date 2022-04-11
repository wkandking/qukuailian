package com.example.qukuailian.controller;

import com.example.qukuailian.bean.Message;
import com.example.qukuailian.util.OPE.CustomException;
import com.example.qukuailian.util.OPE.MessageUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 控制器增强处理(返回 JSON 格式数据)，添加了这个注解的类能被 classpath 扫描自动发现
public class ExceptionHandle {

    @ExceptionHandler(value = Exception.class) // 捕获 Controller 中抛出的指定类型的异常，也可以指定其他异常
    public <E> Message<E> handler(Exception exception){

        if (exception instanceof CustomException){
            CustomException customException = (CustomException) exception;
            return MessageUtil.error(customException.getCode(), customException.getMessage());
        } else {
            return MessageUtil.error(120, "异常信息：" + exception.getMessage());
        }
    }
}
