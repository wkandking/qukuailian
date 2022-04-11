package com.example.qukuailian.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.qukuailian.bean.Message;
import com.example.qukuailian.bean.Supervise;
import com.example.qukuailian.service.CommonService;
import com.example.qukuailian.util.OPE.CustomException;
import com.example.qukuailian.util.OPE.MessageUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Autowired
    CommonService commonService;

    @PostMapping(value = "/getSM3Hash")
    public Message<String> getSM3Hash(@RequestParam("message") String message){
        try {
            String hashValue = commonService.getSM3Method(message);
            return MessageUtil.ok(hashValue);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(120,"SM3加密失败");
        }
    }

    @PostMapping("encryption")
    public Message<String> getTimeSM4Encrypt(@RequestBody String json) {
        JSONObject o = (JSONObject) JSON.parse(json);
        String message = o.getString("message");
        String time = o.getString("timestamp");
        try{
            String encryMessage = commonService.timeEncryptBySM4(message, time);
            return MessageUtil.ok(encryMessage);
        }catch (Exception e){
            e.printStackTrace();
            return MessageUtil.error("加密失败");
        }
    }

    @PostMapping("decryption")
    public Message<String> getTimeSM4Decrypt(@RequestBody String json){
        JSONObject o = (JSONObject) JSON.parse(json);
        String ciphertext = o.getString("ciphertext");
        String time = o.getString("timestamp");
        try{
            String message = commonService.timeDecryptBySM4(ciphertext, time);
            return MessageUtil.ok(message);
        }catch (Exception e){
            e.printStackTrace();
            return MessageUtil.error("解密失败");
        }
    }

    @PostMapping(value = "/difPrivacy")
    public Message<String> difPrivacy(@RequestParam("value") Double value,@RequestParam("epsilon") Double epsilon){
        try {
            Double difValue = commonService.difPrivacy(value,epsilon);
            String retValue = String.valueOf(difValue);
            return MessageUtil.ok(retValue);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(120,"差分隐私加密失败");
        }
    }

    @PostMapping(value = "/kanonymity")
    public Message<String> kanonymity(@RequestParam("value") int value,@RequestParam("message") String message){
        try {
            String retValue = commonService.kanonymity(value,message);
            return MessageUtil.ok(retValue);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(120,"K匿名加密失败");
        }
    }

    @PostMapping(value = "/test")
    public void Test(@RequestBody String rec_json){
        ObjectMapper mapper = new ObjectMapper();
        try {
            Supervise supervise = mapper.readValue(rec_json,Supervise.class);
            System.out.println(supervise);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
