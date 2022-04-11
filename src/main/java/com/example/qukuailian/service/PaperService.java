package com.example.qukuailian.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.qukuailian.bean.*;
import com.example.qukuailian.dao.PaperMapper;
import com.example.qukuailian.dao.UserMapper;
import com.example.qukuailian.util.OPE.CustomException;
import com.example.qukuailian.util.Req.ApiPost;
import com.example.qukuailian.util.SM2.SM2;
import com.example.qukuailian.util.SMA;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaperService {

    @Autowired
    PaperMapper paperMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    CommonService commonService;

    public Paper insert(String json){
        JSONObject o = (JSONObject) JSON.parse(json);
        String paperNumber = o.getString("paperNumber");
        String name = o.getString("issuer");
        User user = userMapper.selectByUserName(name);
        Paper paper = new Paper();
        paper.setPaperId(paperNumber);
        paper.setUserId(user.getUserid());
        int result = paperMapper.insert(paper);
        if(result == 0) throw new CustomException(120,"insert paper failed");
        return paper;
    }

    public PaperInformation getPaperInformation(Paper paper){
        User user =  userMapper.selectByPrimaryKey(paper.getUserId());
        PaperInformation paperInformation = new PaperInformation();
        paperInformation.setIssuer(user.getUsername());
        paperInformation.setPaperNumber(paper.getPaperId());
        paperInformation.setPk(user.getPk());
        paperInformation.setSk(user.getSk());
        paperInformation.setIssuerOrg(user.getOrg());
        paperInformation.setSm4Key(user.getSm4key());

        return paperInformation;
    }

    public PaperInformation encrypt(PaperInformation paperInformation, String algType) throws Exception {

        Method method = commonService.getEncryptMethod(algType);
        PaperInformation result = new PaperInformation();
        String key = "";
        if(algType.equals("2")){
            key = paperInformation.getPk();
        }else{
            key = paperInformation.getSm4Key();
        }
        System.out.println(paperInformation.getNewOwner());
        System.out.println(paperInformation.getNewOwnerOrg());
        result.setPaperNumber(paperInformation.getPaperNumber());
        result.setIssuer((String) method.invoke(null, new Object[]{paperInformation.getIssuer(), key}));
        result.setIssuerOrg((String) method.invoke(null,  new Object[]{paperInformation.getIssuerOrg(),key}));
        result.setPrice((String) method.invoke(null,  new Object[]{paperInformation.getPrice(),key}));
        result.setNewOwner((String) method.invoke(null,  new Object[]{paperInformation.getNewOwner(), key}));
        result.setNewOwnerOrg((String) method.invoke(null,  new Object[]{paperInformation.getNewOwnerOrg(), key}));
        result.setPk(paperInformation.getPk());
        result.setSk(paperInformation.getSk());
        result.setSm4Key(paperInformation.getSm4Key());
        return result;
    }

    public PaperInformation decrypt(PaperInformation paperInformation, String algtype) throws Exception {

        Method method = commonService.getDecryptMethod(algtype);
        String key = "";
        if(algtype.equals("2")){
            key = paperInformation.getSk();
        }else{
            key = paperInformation.getSm4Key();
        }
        PaperInformation result = new PaperInformation();
        System.out.println(paperInformation.getNewOwner());
        result.setPaperNumber(paperInformation.getPaperNumber());
        result.setPrice((String) method.invoke(null, new Object[]{paperInformation.getPrice(),key}));
        result.setIssuer((String) method.invoke(null, new Object[]{paperInformation.getIssuer(),key}));
        result.setIssuerOrg((String) method.invoke(null, new Object[]{paperInformation.getIssuerOrg(), key}));
        result.setNewOwner((String) method.invoke(null, new Object[]{paperInformation.getNewOwner(), key}));
        result.setNewOwnerOrg((String) method.invoke(null, new Object[]{paperInformation.getNewOwnerOrg(), key}));


        result.setPk(paperInformation.getPk());
        result.setSk(paperInformation.getSk());
        result.setSm4Key(paperInformation.getSm4Key());
        return result;
    }

    public PaperInformation updateOwner(String json){
        JSONObject o = (JSONObject) JSON.parse(json);
        String paperNumber = o.getString("paperNumber");
        String issuer = o.getString("issuer");
        String issuerOrg = o.getString("issuerOrg");
        String newOwner = o.getString("newOwner");
        String newOwnerOrg = o.getString("newOwnerOrg");

        User newUser = userMapper.selectByUserName(newOwner);
        Paper paper = new Paper();
        paper.setPaperId(paperNumber);
        paper.setUserId(newUser.getUserid());
        paperMapper.updateByPrimaryKey(paper);

        PaperInformation p = new PaperInformation();
        p.setPaperNumber(paperNumber);
        p.setIssuer(issuer);
        p.setIssuerOrg(issuerOrg);
        p.setNewOwner(newOwner);
        p.setNewOwnerOrg(newOwnerOrg);
        p.setPk(newUser.getPk());
        p.setSk(newUser.getSk());
        p.setSm4Key(newUser.getSm4key());
        return p;
    }


    public PaperInformation convertPaperInforMation(String json) {
        JSONObject o = (JSONObject) JSON.parse(json);
        String paperNumber = o.getString("paperNumber");
        String price = o.getString("price");
        String newOwner = o.getString("newOwner");

        String userid = paperMapper.selectByPrimaryKey(paperNumber).getUserId();
        User user = userMapper.selectByPrimaryKey(userid);

        PaperInformation p = new PaperInformation();
        p.setPaperNumber(paperNumber);
        p.setNewOwner(newOwner);
        p.setPrice(price);
        p.setPk(user.getPk());
        p.setSk(user.getSk());
        p.setSm4Key(user.getSm4key());
        return p;
    }

    public String encrypt(String json) throws Exception {
        JSONObject o = (JSONObject) JSON.parse(json);
        String username = o.getString("username");
        String origintext = o.getString("origintext");
        String algType = o.getString("algType");
        Method method = commonService.getEncryptMethod(algType);
        User user = userMapper.selectByUserName(username);
        return (String) method.invoke(null, new Object[]{origintext, user.getPk()});
    }
    public String decrypt(String json) throws Exception {
        JSONObject o = (JSONObject) JSON.parse(json);
        String username = o.getString("username");
        String encrypttext = o.getString(("encrypttext"));
        String algType = o.getString("algType");
        Method method = commonService.getDecryptMethod(algType);
        User user = userMapper.selectByUserName(username);
        return (String) method.invoke(null, new Object[]{encrypttext, user.getSk()});
    }

    public String owner(String json){
        JSONObject o = (JSONObject) JSON.parse(json);
        String owner = o.getString("owner");
        return userMapper.selectByUserName(owner).getNameencrypt();
    }


    public void reqSuperviseForIssue(String json) throws Exception{
        Supervise supervise = new Supervise();
        JSONObject o = (JSONObject) JSON.parse(json);
        //存放票据号
        String paperNumber = o.getString("paperNumber");
        //存放用户名
        String name = o.getString("issuer");
        //存放组织名
        String org = o.getString("issuerOrg");
        String algorithm = o.getString("algType");

        supervise.setData_id(paperNumber);
        supervise.setOrg(org);
        supervise.setDep("paper-issue");
        //supervise.setCurrentState("1");
        User user = userMapper.selectByUserName(name);
        String user_pk = user.getPk();
        //存放监管者加密的用户私钥
        List l = new ArrayList();
        String super_pk = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEqjP97ZGzNEU1yVjF/b7LC/yaNrL09qy6Gd1+BcVbINLF4pxl28NvaOM+4UTCInPvOWpd3KSg5DWXVTfAOajsTg==";

        String user_sk = user.getSk();
        String enc_usersk = SM2.encrypt(user_sk,super_pk);
        l.add(enc_usersk);
        l.add(enc_usersk);

        supervise.setEncrypted_key(l);
        List l2 = new ArrayList();
        EncryptContent EC1 = new EncryptContent();
        EC1.setType("SM" + algorithm);
        EC1.setEncrypt_data(SM2.encrypt(name,user_pk));
        l2.add(EC1);
        EncryptContent EC2 = new EncryptContent();
        EC2.setType("SM" + algorithm);
        EC2.setEncrypt_data(SM2.encrypt(org,user_pk));
        l2.add(EC2);
        //存放加密后的内容
        supervise.setEncrypted_content(l2);
        ObjectMapper mapper = new ObjectMapper();
        String jsonSupervise = mapper.writeValueAsString(supervise);
        //ApiPost.sendPost("http://localhost:8080/common/test",jsonSupervise);
        ApiPost.sendPost("https://blockchain.nickname4th.vip/api/base/saveprivateprotectinfo",jsonSupervise);
    }

    public void reqSuperviseForBuy(String json) throws Exception{
        Supervise supervise = new Supervise();
        JSONObject o = (JSONObject) JSON.parse(json);
        String paperNumber = o.getString("paperNumber");
        String price = o.getString("price");
        String newOwner = o.getString("newOwner");
        String newOwnerOrg = o.getString("newOwnerOrg");
        String algorithm = o.getString("algType");

        String userid = paperMapper.selectByPrimaryKey(paperNumber).getUserId();
        User user = userMapper.selectByPrimaryKey(userid);
        //存放票据号，组织号，业务类型
        supervise.setData_id(paperNumber);
        supervise.setOrg(user.getOrg());
        supervise.setDep("paper-buy");
        //supervise.setCurrentState("2");
        String super_pk = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEqjP97ZGzNEU1yVjF/b7LC/yaNrL09qy6Gd1+BcVbINLF4pxl28NvaOM+4UTCInPvOWpd3KSg5DWXVTfAOajsTg==";
        List l = new ArrayList();

        String enc_usersk = SM2.encrypt(user.getSk(),super_pk);
        l.add(enc_usersk);
        l.add(enc_usersk);
        l.add(enc_usersk);

        //存放监管者公钥加密的用户私钥
        supervise.setEncrypted_key(l);
        List l2 = new ArrayList();

        EncryptContent EC1 = new EncryptContent();
        EC1.setType("SM" + algorithm);
        EC1.setEncrypt_data(SM2.encrypt(price,user.getPk()));
        l2.add(EC1);
        EncryptContent EC2 = new EncryptContent();
        EC2.setType("SM" + algorithm);
        EC2.setEncrypt_data(SM2.encrypt(newOwner,user.getPk()));
        l2.add(EC2);
        EncryptContent EC3 = new EncryptContent();
        EC3.setType("SM" + algorithm);
        EC3.setEncrypt_data(SM2.encrypt(newOwnerOrg,user.getPk()));
        l2.add(EC3);
        //存放加密后的内容
        supervise.setEncrypted_content(l2);
        ObjectMapper mapper = new ObjectMapper();

        String jsonSupervise = mapper.writeValueAsString(supervise);
        //ApiPost.sendPost("http://localhost:8080/common/test",jsonSupervise);
        ApiPost.sendPost("https://blockchain.nickname4th.vip/api/base/saveprivateprotectinfo",jsonSupervise);
    }

    public void reqSuperviseForTransfer(String json) throws Exception{
        Supervise supervise = new Supervise();
        JSONObject o = (JSONObject) JSON.parse(json);
        String paperNumber = o.getString("paperNumber");
        String issuer = o.getString("issuer");
        String issuerOrg = o.getString("issuerOrg");
        String newOwner = o.getString("newOwner");
        String newOwnerOrg = o.getString("newOwnerOrg");
        String algType = o.getString("algType");
        User user = userMapper.selectByUserName(newOwner);
        User user1 = userMapper.selectByUserName(issuer);
        supervise.setDep("paper-transfer");
        supervise.setOrg(newOwnerOrg);
        supervise.setData_id(paperNumber);

        String super_pk = "MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEqjP97ZGzNEU1yVjF/b7LC/yaNrL09qy6Gd1+BcVbINLF4pxl28NvaOM+4UTCInPvOWpd3KSg5DWXVTfAOajsTg==";
        List l = new ArrayList();
        String enc_usersm4key = SM2.encrypt(user.getSm4key(), super_pk);
        String enc_usersk = SM2.encrypt(user1.getSk(),super_pk);
        if(algType.equals("2")){
            l.add(enc_usersk);
            l.add(enc_usersk);
        }
        else {

            l.add(enc_usersk);
            l.add(enc_usersm4key);
        }

        supervise.setEncrypted_key(l);
        List l2 = new ArrayList();

        EncryptContent EC1 = new EncryptContent();
        EC1.setType("SM2");
        EC1.setEncrypt_data(SM2.encrypt(issuer,user.getPk()));
        l2.add(EC1);
//        EncryptContent EC2 = new EncryptContent();
//        EC2.setType("SM" + algType);
//        EC2.setEncrypt_data(SM2.encrypt(issuerOrg,user.getPk()));
//        l2.add(EC2);
        EncryptContent EC3 = new EncryptContent();
        EC3.setType("SM" + algType);
        EC3.setEncrypt_data(SM2.encrypt(newOwner,user.getPk()));
        l2.add(EC3);
//        EncryptContent EC4 = new EncryptContent();
//        EC4.setType("SM" + algType);
//        EC4.setEncrypt_data(SM2.encrypt(newOwnerOrg,user.getPk()));
//        l2.add(EC4);
        supervise.setEncrypted_content(l2);
        ObjectMapper mapper = new ObjectMapper();
        String jsonSupervise = mapper.writeValueAsString(supervise);
        //ApiPost.sendPost("http://localhost:8080/common/test",jsonSupervise);
        //ApiPost.sendPost("https://blockchain.nickname4th.vip/api/base/saveprivateprotectinfo",jsonSupervise);
        ApiPost.sendPost("https://blockchain.nickname4th.vip/api/base/saveprivateprotectinfo",jsonSupervise);
    }
}
