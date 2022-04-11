package com.example.qukuailian.bean;

public class UserWithBLOBs extends User {
    private byte[] cert;

    private byte[] sigCert;

    public byte[] getCert() {
        return cert;
    }

    public void setCert(byte[] cert) {
        this.cert = cert;
    }

    public byte[] getSigCert() {
        return sigCert;
    }

    public void setSigCert(byte[] sigCert) {
        this.sigCert = sigCert;
    }
}