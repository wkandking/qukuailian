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
    ConcurrentHashMap<String, Long> auctionMap;

    // key : 交易ID , Value : 交易时间 - 毫秒
    ConcurrentHashMap<String, Long> paperMap;

    String paperCert;
    String auctionCert;

    public String getPaperCert() {
        return "e05d1548f3f7b649d748e9d3d61f527c15e39bcb";
    }

    public String getAuctionCert() {
        return "d253ae61230617eaf5501c4581f2808ac8371d66";
    }

    CheckService() {
        this.auctionMap = new ConcurrentHashMap<>();
        this.paperMap = new ConcurrentHashMap<>();
    }

    CheckService(long duration) {
        this.auctionMap = new ConcurrentHashMap<>();
        this.paperMap = new ConcurrentHashMap<>();
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    private Long getNow() {
        return new Date().getTime();
    }

    public boolean checkAuction(String username) {
        Long now = getNow();
        if (!auctionMap.containsKey(username)) {
            return false;
        } else {
            if (auctionMap.get(username) - getDuration() - now < 0) {
                auctionMap.remove(username);
                return false;
            }
        }

        auctionMap.put(username, now);
        return true;
    }

    public boolean checkPaper(String papernum) {
        Long now = getNow();
        if (!paperMap.containsKey(papernum)) {
            return false;
        } else {
            if (paperMap.get(papernum) - getDuration() - now < 0) {
                paperMap.remove(papernum);
                return false;
            }
        }

        paperMap.put(papernum, now);
        return true;
    }

    public void putAuctionMap(String username, Long time) {
        this.auctionMap.put(username, time);
    }

    public void putPaperMap(String papernum, Long time) {
        this.paperMap.put(papernum, time);
    }
}
