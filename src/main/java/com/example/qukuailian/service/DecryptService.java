package com.example.qukuailian.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.qukuailian.util.SM4.SM4Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DecryptService {

    @Autowired
    SM4Utils sm4;

    public String DecryptString (String plainText, String key) {
        sm4.secretKey = key;
        return sm4.decryptData_ECB(plainText);
    }

    public JSONObject DecryptJson (String plainText, String key) {
        String json = DecryptString(plainText, key);
        return (JSONObject) JSON.parse(json);
    }
}
