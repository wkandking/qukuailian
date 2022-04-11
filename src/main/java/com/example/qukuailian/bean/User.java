package com.example.qukuailian.bean;

public class User {
    private String userid;

    private String username;

    private String org;

    private String pk;

    private String sk;

    private String nameencrypt;

    private String sm4key;

    private String sigPk;

    private String sigSk;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid == null ? null : userid.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org == null ? null : org.trim();
    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk == null ? null : pk.trim();
    }

    public String getSk() {
        return sk;
    }

    public void setSk(String sk) {
        this.sk = sk == null ? null : sk.trim();
    }

    public String getNameencrypt() {
        return nameencrypt;
    }

    public void setNameencrypt(String nameencrypt) {
        this.nameencrypt = nameencrypt == null ? null : nameencrypt.trim();
    }

    public String getSm4key() {
        return sm4key;
    }

    public void setSm4key(String sm4key) {
        this.sm4key = sm4key == null ? null : sm4key.trim();
    }

    public String getSigPk() {
        return sigPk;
    }

    public void setSigPk(String sigPk) {
        this.sigPk = sigPk == null ? null : sigPk.trim();
    }

    public String getSigSk() {
        return sigSk;
    }

    public void setSigSk(String sigSk) {
        this.sigSk = sigSk == null ? null : sigSk.trim();
    }
}