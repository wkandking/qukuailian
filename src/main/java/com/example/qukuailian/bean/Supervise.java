package com.example.qukuailian.bean;

import java.util.List;

public class Supervise {

    private String data_id;

    private String dep;

    private String org;

    private List encrypted_content;

    private List encrypted_key;


    public String getData_id() {
        return data_id;
    }

    public void setData_id(String data_id) {
        this.data_id = data_id;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public List getEncrypted_content() {
        return encrypted_content;
    }

    public void setEncrypted_content(List encrypted_content) {
        this.encrypted_content = encrypted_content;
    }

    public List getEncrypted_key() {
        return encrypted_key;
    }

    public void setEncrypted_key(List encrypted_key) {
        this.encrypted_key = encrypted_key;
    }


    @Override
    public String toString() {
        return "Supervise{" +
                "data_id='" + data_id + '\'' +
                ", dep='" + dep + '\'' +
                ", org='" + org + '\'' +
                ", encrypted_content=" + encrypted_content +
                ", encrypted_key=" + encrypted_key +
                '}';
    }
}