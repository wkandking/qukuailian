package com.example.qukuailian.service;

import com.example.qukuailian.bean.*;
import com.example.qukuailian.dao.AuctionMapper;
import com.example.qukuailian.dao.UserMapper;
import com.example.qukuailian.util.OPE.MessageUtil;
import com.example.qukuailian.util.OPE.OPE;
import com.example.qukuailian.util.Req.ApiPost;
import com.example.qukuailian.util.SM2.SM2;
import com.example.qukuailian.util.SMA;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Service
public class AuctionService {
    @Autowired
    AuctionMapper auctionMapper;

    @Autowired
    UserService userService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    CommonService commonService;

    public int insertAuction(String auctionId){
        Auction auction = new Auction();
        auction.setAuctionId(auctionId);
        auctionMapper.insert(auction);
        Integer flag = auctionMapper.selectByAuctionId(auctionId).getFlag();
        System.out.println(flag);
        KeyPair keyPair = SM2.generateSm2KeyPair(String.valueOf(flag));
        String pk = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
        String sk = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
        auction.setFlag(flag);
        auction.setPk(pk);
        auction.setSk(sk);
        auction.setOk(auctionId);
        return auctionMapper.updateByPrimaryKey(auction);
    }

    public AuctionInformation encrypt(AuctionInformation auctionInformation) throws Exception {
        AuctionInformation result = new AuctionInformation();
        Auction auction = auctionMapper.selectByAuctionId(auctionInformation.getAuctionId());
        result.setAuctionId(auctionInformation.getAuctionId());
        result.setBidprice(OPE.getInstance().encrypt(new BigInteger(auctionInformation.getBidprice())).toString());
        result.setUsername(SM2.encrypt(auctionInformation.getUsername(),auction.getPk()));
        return result;
    }

    public AuctionInformation decrypt(AuctionInformation auctionInformation) throws Exception {
        AuctionInformation result = new AuctionInformation();
        Auction auction = auctionMapper.selectByAuctionId(auctionInformation.getAuctionId());

        result.setAuctionId(auctionInformation.getAuctionId());
        result.setBidprice(OPE.getInstance().decrypt(new BigInteger(auctionInformation.getBidprice())).toString());
        result.setUsername(SM2.decrypt(auctionInformation.getUsername(),auction.getSk()));
        return result;

    }

    public Pair<Boolean, User> insertKey(String algtype, String username) {
        try{
            User user = userMapper.selectByUserName(username);
            System.out.println(user);
            if(user == null){
                userService.insertUserFromAuction(username);
                user = userMapper.selectByUserName(username);
                return new Pair<>(true, user);
            }
            return new Pair<>(false, user);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

    public String selectKey(String username) {
        try{
            User user = userMapper.selectByUserName(username);
            System.out.println(user);
            return user.getSk();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public String pkenc(String algtype, String pk, String username) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = commonService.getEncryptMethod(algtype);
        String key = pk.replaceAll(" +","+");
        String data = (String) method.invoke(null, new Object[]{username, key});
        return data;
    }

    public String decinfo(String algtype, String sk, String enc_username) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = commonService.getDecryptMethod(algtype);
        String data = (String) method.invoke(null, new Object[]{enc_username, sk});

        return data;
    }

    public void reqSupervise(String auctionId,String bidprice,String username,String stype) throws Exception{
        Supervise supervise = new Supervise();
        //存放拍卖号
        supervise.setData_id(auctionId);
        //存放加密内容
        Auction auction = auctionMapper.selectByAuctionId(auctionId);
        List l = new ArrayList();
        EncryptContent EC1 = new EncryptContent();
        EC1.setType("OPE");
        EC1.setEncrypt_data(OPE.getInstance().encrypt(new BigInteger(bidprice)).toString());
        l.add(EC1);
        EncryptContent EC2 = new EncryptContent();
        EC2.setType("SM2");
        EC2.setEncrypt_data(SM2.encrypt(username,auction.getPk()));
        l.add(EC2);
        supervise.setEncrypted_content(l);
        //存放业务类型
        if(stype.equals("1")) {
            supervise.setDep("auction-condition");
        }
        else{
            supervise.setDep("auction-protect");
        }
        //存放组织号
        supervise.setOrg("Org1");
        //存放经监管者公钥加密的密钥信息
        String super_pk = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEqjP97ZGzNEU1yVjF/b7LC/yaNrL09qy6Gd1+BcVbINLF4pxl28NvaOM+4UTCInPvOWpd3KSg5DWXVTfAOajsTg==";
        String ec_sm2sk = SM2.encrypt(auction.getSk(),super_pk);
        String order_key = "privacyprotect";
        String ec_orderkey = SM2.encrypt(order_key,super_pk);
        List l2 = new ArrayList();
        l2.add(ec_orderkey);
        l2.add(ec_sm2sk);
        supervise.setEncrypted_key(l2);
        ObjectMapper mapper = new ObjectMapper();
        String jsonSupervise = mapper.writeValueAsString(supervise);
        //System.out.println(jsonSupervise);
        //ApiPost.sendPost("http://localhost:8080/common/test",jsonSupervise);
        ApiPost.sendPost("https://blockchain.nickname4th.vip/api/base/saveprivateprotectinfo",jsonSupervise);
    }
}
