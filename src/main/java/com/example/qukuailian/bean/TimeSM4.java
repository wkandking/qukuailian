package com.example.qukuailian.bean;

public class TimeSM4 {
    private String tstring;

    private String sm4key;

    public String getTstring() {
        return tstring;
    }

    public void setTstring(String tstring) {
        this.tstring = tstring == null ? null : tstring.trim();
    }

    public String getSm4key() {
        return sm4key;
    }

    public void setSm4key(String sm4key) {
        this.sm4key = sm4key == null ? null : sm4key.trim();
    }
}