package com.example.qukuailian.bean;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Paper {
    private String txid;

    private String papernumber;

    private String buyer;

}