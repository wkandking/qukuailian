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

    @Autowired
    CheckService checkService;

    public String DecryptString (String plainText, String username) {
        if (checkService.check(username)) {
            sm4.secretKey = checkService.map.get(username).getKey();
            return sm4.decryptData_ECB(plainText);
        } else return null;
    }

    public JSONObject DecryptJson (String plainText, String username) {
        String json = DecryptString(plainText, username);
        return (JSONObject) JSON.parse(json);
    }
}
