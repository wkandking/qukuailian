package com.example.qukuailian.controller.encrypted;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.qukuailian.bean.Message;
import com.example.qukuailian.bean.Paper;
import com.example.qukuailian.bean.PaperInformation;
import com.example.qukuailian.service.DecryptService;
import com.example.qukuailian.service.PaperService;
import com.example.qukuailian.service.UserService;
import com.example.qukuailian.util.OPE.CustomException;
import com.example.qukuailian.util.OPE.MessageUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/encrypted/paper")
public class EncryptedPaperController {

    @Autowired
    UserService userService;

    @Autowired
    PaperService paperService;

    @Autowired
    DecryptService decryptService;

    /**
     * 根据接受的参数生成密钥对保存在数据库表 users
     **/
    @PostMapping(value = "/register")
    public Message<String> register(@RequestParam String username,
                                    @RequestParam String json) {
        try {
            json = decryptService.DecryptString(json, username);
        } catch (Exception e) {
            throw new CustomException(120, "register insert failed!" + e.getMessage());
        }

        boolean result = userService.checkUserIsEmpty(json);
        if (result) {
            try {
                userService.insertUser(json);
                return MessageUtil.ok();
            } catch (Exception e) {
                throw new CustomException(120, "register insert failed!" + e.getMessage());
            }

        }
        return MessageUtil.error(120, "userid&org已存在");
    }

    @PostMapping("/issue")
    public Message<PaperInformation> issue(@RequestParam String username,
                                           @RequestParam String json) {
        try {
            json = decryptService.DecryptString(json, username);

            Paper paper = paperService.insert(json);
            JSONObject o = (JSONObject) JSON.parse(json);
            String algType = o.getString("algType");
            PaperInformation paperInformation = paperService.getPaperInformation(paper);

            paperService.reqSuperviseForIssue(json);
            PaperInformation p = paperService.encrypt(paperInformation, algType);
            p.setSk(null);
            return MessageUtil.ok(p);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(120, "issue is failed ! " + e.getMessage());
        }
    }

    @PostMapping("/buy")
    public Message<PaperInformation> buy(@RequestParam String username,
                                         @RequestParam String json) {
        try {
            json = decryptService.DecryptString(json, username);

            PaperInformation p = paperService.convertPaperInforMation(json);
            JSONObject o = (JSONObject) JSON.parse(json);
            String algType = o.getString("algType");

            paperService.reqSuperviseForBuy(json);
            p = paperService.encrypt(p, algType);
            p.setPk(null);
            p.setSk(null);
            return MessageUtil.ok(p);
        } catch (Exception e) {
            throw new CustomException(120, "issue encrypt failed!" + e.getMessage());
        }

    }

    @PostMapping("/check")
    public Message<PaperInformation> check(@RequestParam String username,
                                           @RequestParam String json) {
        try {
            json = decryptService.DecryptString(json, username);

            PaperInformation p = paperService.convertPaperInforMation(StringEscapeUtils.unescapeHtml(json));
            JSONObject o = (JSONObject) JSON.parse(json);
            String algType = o.getString("algType");
            p = paperService.decrypt(p, algType);
            p.setPk(null);
            p.setSk(null);
            return MessageUtil.ok(p);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(120, "issue decrypt failed! " + e.getMessage());
        }
    }

    @PostMapping("/transfer")
    public Message<PaperInformation> transfer(@RequestParam String username,
                                              @RequestParam String json) {
        try {
            json = decryptService.DecryptString(json, username);

            PaperInformation p = paperService.updateOwner(json);
            JSONObject o = (JSONObject) JSON.parse(json);
            String algType = o.getString("algType");
            paperService.reqSuperviseForTransfer(json);
            p = paperService.encrypt(p, algType);
            p.setSk(null);
            return MessageUtil.ok(p);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(120, "transfer is failed! " + e.getMessage());
        }
    }

    @PostMapping("/encrypt")
    public Message<String> encrypt(@RequestParam String username,
                                   @RequestParam String json) throws Exception {
        try {
            json = decryptService.DecryptString(json, username);
            return MessageUtil.ok(paperService.encrypt(json));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(120, "encrypt is failed! " + e.getMessage());
        }
    }

    @PostMapping("/decrypt")
    public Message<String> decrypt(@RequestParam String username,
                                   @RequestParam String json) throws Exception {
        try {
            json = decryptService.DecryptString(json, username);
            return MessageUtil.ok(paperService.decrypt(json));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(120, "decrypt is failed! " + e.getMessage());
        }
    }

    @PostMapping("/owner")
    public Message<String> owner(@RequestParam String username,
                                 @RequestParam String json) throws Exception {
        try {
            json = decryptService.DecryptString(json, username);
            return MessageUtil.ok(paperService.owner(json));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(120, "get owner is failed! " + e.getMessage());
        }
    }
}