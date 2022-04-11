package com.example.qukuailian.util.Other;

public class KanonymityImpl {
    public static String kannoy(int k, String content){
        StringBuffer sb = new StringBuffer();
        if(content.length() < 100){
            for(int i = content.length(); i < 100; i++){
                sb.append('0');
            }
        }
        sb.append(content);
        k = (int)Math.floor(Math.log(k) / Math.log(10));
        for(int i = 0; i < k; i++){
            sb.replace(sb.length() - 1 - i, sb.length() - i, "*");
        }
        return sb.toString();
    }
}
