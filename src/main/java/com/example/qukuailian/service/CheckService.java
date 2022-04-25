package com.example.qukuailian.service;

import com.example.qukuailian.bean.Message;
import com.example.qukuailian.util.OPE.MessageUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CheckService {

    // 持续时间默认为 半个小时
    long duration = 30 * 60 * 1000;

    // key : 交易人姓名 , Value : 交易时间 - 毫秒
    ConcurrentHashMap<String, Long> map;

    String paperCert;
    String auctionCert;

    public String getPaperCert() {
        return "e05d1548f3f7b649d748e9d3d61f527c15e39bcb";
    }

    public String getAuctionCert() {
        return "d253ae61230617eaf5501c4581f2808ac8371d66";
    }

    CheckService() {
        this.map = new ConcurrentHashMap<>();
    }

    CheckService(long duration) {
        this.map = new ConcurrentHashMap<>();
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    public boolean check(String username) {
        Long now = System.currentTimeMillis();
        if (!map.containsKey(username)) {
            return false;
        } else {
            if (map.get(username) + getDuration() - now < 0) {
                map.remove(username);
                System.out.println("check overtime!");
                return false;
            }
        }

        System.out.println("check successfully!");
        map.put(username, now);
        return true;
    }

    public void putAuctionMap(String username, Long time) {
        this.map.put(username, time);
    }

    public void putPaperMap(String username, Long time) {
        this.map.put(username, time);
    }
}
