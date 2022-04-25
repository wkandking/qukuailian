package com.example.qukuailian.controller.encrypted;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.qukuailian.bean.AuctionInformation;
import com.example.qukuailian.bean.Message;
import com.example.qukuailian.bean.User;
import com.example.qukuailian.service.AuctionService;
import com.example.qukuailian.service.DecryptService;
import com.example.qukuailian.util.OPE.CustomException;
import com.example.qukuailian.util.OPE.MessageUtil;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/encrypted/auction")

public class EncryptedAuctionController {

    @Autowired
    AuctionService auctionService;

    @Autowired
    DecryptService decryptService;

    @PostMapping("/createkey")

//    public Message<User> createKey(@RequestParam("algtype") String algtype,
//                                   @RequestParam("username") String username) throws Exception {

    public Message<User> createKey(@RequestParam String username,
                                   @RequestParam String json) throws Exception {
        String algtype = decryptService.DecryptJson(json, username).getString("algtype");
        Pair<Boolean, User> pair = auctionService.insertKey(algtype, username);
        if(pair.getKey()){
            return MessageUtil.ok(pair.getValue());
        }
        return MessageUtil.error(pair.getValue());
    }

    @PostMapping("/pkenc")
    public Message<String> pkenc(@RequestBody String username,
                                 @RequestBody String json) throws Exception {
        JSONObject o = decryptService.DecryptJson(json, username);
        String algtype = o.getString("algtype");
        String pk = o.getString("pk");
        System.out.println(pk);
        System.out.println(username);
        String enc_username = auctionService.pkenc(algtype, pk ,username);
        return MessageUtil.ok(enc_username);
    }


//    @PostMapping("/decinfo")
//    public Message<String> decinfo(@RequestBody String json) throws Exception {
//        JSONObject o = (JSONObject) JSON.parse(json);
//        String algtype = o.getString("algtype");
//        String sk = o.getString("sk");
//        String enc_username = o.getString("enc_username");
//        System.out.println(sk);
//        System.out.println(enc_username);
//        try{
//            String username = auctionService.decinfo(algtype, sk, enc_username);
//            return MessageUtil.ok(username);
//        }catch (Exception e){
//            e.printStackTrace();
//            throw new CustomException(120, e.getMessage());
//        }
//
//    }

    @PostMapping("/decinfo")
    public Message<String> decinfo(@RequestBody String username,
                                   @RequestBody String json) throws Exception {
        JSONObject o = decryptService.DecryptJson(json, username);
        String algtype = o.getString("algtype");
        String username1 = o.getString("username");
        String sk = auctionService.selectKey(username1);
        String enc_username = o.getString("enc_username");
        System.out.println(sk);
        System.out.println(enc_username);
        try{
            String username2 = auctionService.decinfo(algtype, sk, enc_username);
            return MessageUtil.ok(username);
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(120, e.getMessage());
        }
    }

    @PostMapping("/createAuction")
    public Message<String> createAuction(@RequestParam("username") String username,
                                         @RequestParam("auctionid") String auctionId){
        try {
            auctionId = decryptService.DecryptString(auctionId, username);
            auctionService.insertAuction(auctionId);
            return MessageUtil.ok();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(120, e.getMessage());
        }
    }

    @RequestMapping("/encBidInfo")
    public Message<AuctionInformation> encBidInfo(@RequestParam("username") String username,
                                                  @RequestParam("json") String json) {
//                                                  @RequestParam("auctionid") String auctionId,
//                                                  @RequestParam("bidprice") String bidprice,
//                                                  @RequestParam("stype") String stype) throws Exception {
        try {
            JSONObject o = decryptService.DecryptJson(json, username);

            String auctionId = o.getString("auctionId");
            String bidprice = o.getString("bidprice");
            String stype = o.getString("stype");

            AuctionInformation auctionInformation = new AuctionInformation();
            auctionInformation.setAuctionId(auctionId);
            auctionInformation.setBidprice(bidprice);
            auctionInformation.setUsername(username);
            auctionService.reqSupervise(auctionId,bidprice,username,stype);
            return MessageUtil.ok(auctionService.encrypt(auctionInformation));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(120, e.getMessage());
        }
    }

    @RequestMapping("/queryInfo")
    public Message<AuctionInformation> queryInfo(@RequestParam("username") String username,
                                                 @RequestParam("json") String json) {

//                                                 @RequestParam("auctionid") String auctionId,
//                                                 @RequestParam("highTestPrice") String highTestPrice,
//                                                 @RequestParam("username") String username) throws Exception {
        try{
            JSONObject o = decryptService.DecryptJson(json, username);

            String auctionId = o.getString("auctionId");
            String highTestPrice = o.getString("highTestPrice");

            AuctionInformation auctionInformation = new AuctionInformation();
            auctionInformation.setAuctionId(auctionId);
            auctionInformation.setBidprice(highTestPrice);
            auctionInformation.setUsername(username);
            return MessageUtil.ok(auctionService.decrypt(auctionInformation));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }
}