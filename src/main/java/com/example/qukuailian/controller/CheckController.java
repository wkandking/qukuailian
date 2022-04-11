package com.example.qukuailian.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.qukuailian.bean.Message;
import com.example.qukuailian.service.CheckService;
import com.example.qukuailian.service.CertService;
import com.example.qukuailian.util.OPE.CustomException;
import com.example.qukuailian.util.OPE.MessageUtil;
import com.example.qukuailian.util.SM3.SM3Service.SM3ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/check")

public class CheckController {

    @Autowired
    CheckService checkService;

    RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/auction")
    public Message<String> checkAuction(@RequestBody String json){
        try{
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            JSONObject o = (JSONObject) JSON.parse(json);
            String cert = o.getString("cert");

            if (!cert.equals(checkService.getAuctionCert()))
                return MessageUtil.error("Cert Error");

            String username = o.getString("username");

            if (checkService.checkAuction(username)) {
                return MessageUtil.ok("ok");
            }

            String randNum = o.getString("rand");
            String hash = SM3ServiceImpl.getHash(cert + username + randNum);

            Map<String,String> params = new HashMap<>();
            params.put("username", username);
            params.put("hash", hash);

            String url = "http://" + getUrl(request) + ":8084/auction/recheck";
            System.out.print("Recheck URL : " + url);
            Message message = restTemplate.getForObject(url, Message.class, params);
            System.out.println("Message : " + message);

            if (message.getCode() == 200) {
                checkService.putAuctionMap(username, message.getTime());
            }

            return MessageUtil.ok("ok");
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(120, e.getMessage());
        }
    }


    @PostMapping("/paper")
    public Message<String> checkPaper(@RequestBody String json) {
        try{
            HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
            JSONObject o = (JSONObject) JSON.parse(json);
            String cert = o.getString("cert");

            if (!cert.equals(checkService.getPaperCert()))
                return MessageUtil.error("Cert Error");

            String papernumber = o.getString("papernumber");

            if (checkService.checkPaper(papernumber)) {
                System.out.println("已存在！");
                return MessageUtil.ok("ok");
            }

            String randNum = o.getString("rand");
            String hash = SM3ServiceImpl.getHash(cert + papernumber + randNum);

            Map<String,String> params = new HashMap<>();
            params.put("papernumber", papernumber);
            params.put("hash", hash);

            String url = "http://" + getUrl(request) + ":8000/base/recheck";
            System.out.print(url);
            String message = restTemplate.postForObject(url, params, String.class);
            System.out.println("Message : " + message);
            JSONObject o2 = (JSONObject) JSON.parse(message);

            if (o2.getInteger("code") == 200) {
                checkService.putPaperMap(papernumber, o2.getLong("time"));
            }

            return MessageUtil.ok("ok");
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(120, e.getMessage());
        }

    }

    private String getUrl (HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
