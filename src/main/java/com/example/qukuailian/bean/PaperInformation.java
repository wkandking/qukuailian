package com.example.qukuailian.bean;

import lombok.Data;

@Data
public class PaperInformation {
    private String paperNumber;
    private String price;
    private String issuer;
    private String issuerOrg;
    private String newOwner;
    private String newOwnerOrg;
    private String pk;
    private String sk;
    private String sm4Key;
}
