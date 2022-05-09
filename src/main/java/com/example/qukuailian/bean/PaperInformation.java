package com.example.qukuailian.bean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaperInformation {
    private String paperNumber;
    private String algType;
    private String buyer;
    private String price;
    private String proctectContent;
    private String txid;
}
