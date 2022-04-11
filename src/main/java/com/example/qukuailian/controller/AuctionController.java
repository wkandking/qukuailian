package com.example.qukuailian.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.qukuailian.bean.AuctionInformation;
import com.example.qukuailian.bean.Message;
import com.example.qukuailian.bean.User;
import com.example.qukuailian.service.AuctionService;
import com.example.qukuailian.util.OPE.CustomException;
import com.example.qukuailian.util.OPE.MessageUtil;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auction")

public class AuctionController {

    @Autowired
    AuctionService auctionService;

    @PostMapping("/createkey")
    public Message<User> createKey(@RequestParam("algtype") String algtype,
                                     @RequestParam("username") String username) throws Exception {
        Pair<Boolean, User> pair = auctionService.insertKey(algtype, username);
        if(pair.getKey()){
            return MessageUtil.ok(pair.getValue());
        }
        return MessageUtil.error(pair.getValue());
    }

    @PostMapping("/pkenc")
    public Message<String> pkenc(@RequestBody String json) throws Exception {
        JSONObject o = (JSONObject) JSON.parse(json);
        String algtype = o.getString("algtype");
        String pk = o.getString("pk");
        String username = o.getString("username");
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
    public Message<String> decinfo(@RequestBody String json) throws Exception {
        JSONObject o = (JSONObject) JSON.parse(json);
        String algtype = o.getString("algtype");
        String username1 = o.getString("username");
        String sk = auctionService.selectKey(username1);
        String enc_username = o.getString("enc_username");
        System.out.println(sk);
        System.out.println(enc_username);
        try{
            String username = auctionService.decinfo(algtype, sk, enc_username);
            return MessageUtil.ok(username);
        }catch (Exception e){
            e.printStackTrace();
            throw new CustomException(120, e.getMessage());
        }
    }

    @PostMapping("/createAuction")
    public Message<String> createAuction(@RequestParam("auctionid") String auctionId){
        auctionService.insertAuction(auctionId);
        return MessageUtil.ok();
    }

    @RequestMapping("/encBidInfo")
    public Message<AuctionInformation> encBidInfo(@RequestParam("auctionid") String auctionId,
                                                  @RequestParam("bidprice") String bidprice,
                                                  @RequestParam("username") String username,
                                                  @RequestParam("stype") String stype) throws Exception {
        AuctionInformation auctionInformation = new AuctionInformation();
        auctionInformation.setAuctionId(auctionId);
        auctionInformation.setBidprice(bidprice);
        auctionInformation.setUsername(username);
        auctionService.reqSupervise(auctionId,bidprice,username,stype);
        return MessageUtil.ok(auctionService.encrypt(auctionInformation));
    }

    @RequestMapping("/queryInfo")
    public Message<AuctionInformation> queryInfo(@RequestParam("auctionid") String auctionId,
                                                 @RequestParam("highTestPrice") String highTestPrice,
                                                 @RequestParam("username") String username) throws Exception {
        try{
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
