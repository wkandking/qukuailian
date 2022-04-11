package com.example.qukuailian.util.OPE;

public class CustomException extends RuntimeException{

    /**
     * 状态码
     */
    private Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public CustomException(Integer code, String message){

        super(message);
        this.code = code;

    }
}
