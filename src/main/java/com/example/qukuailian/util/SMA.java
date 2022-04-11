package com.example.qukuailian.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@AllArgsConstructor
public enum SMA {

    SM2("com.example.qukuailian.util.SM2.SM2"),
    SM3("com.example.qukuailian.util.SM3.SM3Service.SM3ServiceImpl"),
    SM4("com.example.qukuailian.util.SM4.SM4Util");

    public String getClassName() {
        return className;
    }

    private String className;

}
