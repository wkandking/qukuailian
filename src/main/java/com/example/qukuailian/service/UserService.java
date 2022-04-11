package com.example.qukuailian.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.qukuailian.bean.User;
import com.example.qukuailian.bean.UserWithBLOBs;
import com.example.qukuailian.dao.UserMapper;
import com.example.qukuailian.util.SM2.SM2;
import com.example.qukuailian.util.SM4.SM4Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Base64;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public boolean checkUserIsEmpty(String json){
        JSONObject o = (JSONObject) JSON.parse(json);
        String userid = o.getString("userid");
        return userMapper.selectByPrimaryKey(userid) == null;
    }

    public User getUserByUserId(String json){
        JSONObject o = (JSONObject) JSON.parse(json);
        String userid = o.getString("userid");
        return userMapper.selectByPrimaryKey(userid);
    }

    public User getUserByUserName(String json){
        JSONObject o = (JSONObject) JSON.parse(json);
        String username = o.getString("issuer");
        return userMapper.selectByUserName(username);
    }

    private UserWithBLOBs genUser(String userid, String username, String org) {
        try {
            KeyPair keyPair = SM2.generateSm2KeyPair(userid);

            String pk = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String sk = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            byte[] cert = CertService.GenerateCertificateBySubKP(username, org, keyPair);

            KeyPair sigKeyPair = SM2.generateSm2KeyPair("123" + userid);
            String sigpk = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
            String sigsk = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());
            byte[] sigCert = CertService.GenerateCertificateBySubKP(username, org, sigKeyPair);

            String nameEncrypt = SM2.encrypt(username, pk);
            String sm4Key = SM4Util.generateKey(userid);
            UserWithBLOBs user = new UserWithBLOBs();
            user.setUserid(userid);
            user.setUsername(username);
            user.setOrg(org);
            user.setPk(pk);
            user.setSk(sk);
            user.setNameencrypt(nameEncrypt);
            user.setSm4key(sm4Key);
            user.setCert(cert);
            user.setSigPk(sigpk);
            user.setSigSk(sigsk);
            user.setSigCert(sigCert);

            return user;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("生成用户失败！");
            return null;
        }
    }

    public int insertUser(String json) {
        JSONObject o = (JSONObject) JSON.parse(json);
        String userid = o.getString("userid");
        String username = o.getString("name");
        String org = o.getString("org");
        UserWithBLOBs user = genUser(userid, username, org);
        return userMapper.insert(user);
    }

    public int insertUserFromAuction(String username) {
        String userid = String.valueOf(System.currentTimeMillis());
        String org = "";
        UserWithBLOBs user = genUser(userid, username, org);
        return userMapper.insert(user);
    }

    public User getUser(String username){
        return userMapper.selectByUserName(username);
    }

}
