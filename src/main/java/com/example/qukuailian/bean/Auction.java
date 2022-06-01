package com.example.qukuailian.bean;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Auction {
    private String auctionId;

    private String pk;

    private String sk;

    private String ok;
}