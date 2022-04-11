package com.example.qukuailian.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.qukuailian.bean.Message;
import com.example.qukuailian.bean.Paper;
import com.example.qukuailian.bean.PaperInformation;
import com.example.qukuailian.service.PaperService;
import com.example.qukuailian.service.UserService;
import com.example.qukuailian.util.OPE.CustomException;
import com.example.qukuailian.util.OPE.MessageUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paper")
public class PaperController {
    @Autowired
    UserService userService;

    @Autowired
    PaperService paperService;

    /** 根据接受的参数生成密钥对保存在数据库表 users
     **/
    @PostMapping(value = "/register")
    public Message<String> register(@RequestBody String json){

        boolean result = userService.checkUserIsEmpty(json);
        if(result) {
            try {
                userService.insertUser(json);
                return MessageUtil.ok();
            }catch (Exception e){
                throw new CustomException(120,"register insert failed!");
            }

        }
        return MessageUtil.error(120,"userid&org已存在");
    }

    @PostMapping("/issue")
    public Message<PaperInformation> issue(@RequestBody String json){
        Paper paper = paperService.insert(json);
        JSONObject o = (JSONObject) JSON.parse(json);
        String algType = o.getString("algType");
        PaperInformation paperInformation = paperService.getPaperInformation(paper);
        try{
            paperService.reqSuperviseForIssue(json);
            PaperInformation p = paperService.encrypt(paperInformation, algType);
            p.setSk(null);
            return MessageUtil.ok(p);
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(120,e.getMessage());
        }
    }

    @PostMapping("/buy")
    public Message<PaperInformation> buy(@RequestBody String json){

        PaperInformation p = paperService.convertPaperInforMation(json);
        JSONObject o = (JSONObject) JSON.parse(json);
        String algType = o.getString("algType");
        try{
            paperService.reqSuperviseForBuy(json);
            p = paperService.encrypt(p, algType);
            p.setPk(null);
            p.setSk(null);
            return MessageUtil.ok(p);
        }catch (Exception e){
            throw new CustomException(120,"issue encrypt failed!");
        }

    }
    @PostMapping("/check")
    public Message<PaperInformation> check(@RequestBody String json){
        try{
            PaperInformation p = paperService.convertPaperInforMation(StringEscapeUtils.unescapeHtml(json));
            JSONObject o = (JSONObject) JSON.parse(json);
            String algType = o.getString("algType");
            p = paperService.decrypt(p, algType);
            p.setPk(null);
            p.setSk(null);
            return MessageUtil.ok(p);
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(120,"issue decrypt failed!");
        }
    }

    @PostMapping("/transfer")
    public Message<PaperInformation> transfer(@RequestBody String json){
        PaperInformation p = paperService.updateOwner(json);
        JSONObject o = (JSONObject) JSON.parse(json);
        String algType = o.getString("algType");
        try{
            paperService.reqSuperviseForTransfer(json);
            p = paperService.encrypt(p, algType);
            p.setSk(null);
            return MessageUtil.ok(p);
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(120,"transfer encrypt failed!");
        }
    }

    @PostMapping("/encrypt")
    public Message<String> encrypt(@RequestBody String json) throws Exception {
        return MessageUtil.ok(paperService.encrypt(json));
    }

    @PostMapping("/decrypt")
    public Message<String> decrypt(@RequestBody String json) throws Exception {
        return MessageUtil.ok(paperService.decrypt(json));
    }

    @PostMapping("/owner")
    public Message<String> owner(@RequestBody String json){
        return MessageUtil.ok(paperService.owner(json));
    }
}
