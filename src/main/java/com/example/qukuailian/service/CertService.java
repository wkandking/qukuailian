package com.example.qukuailian.service;

import com.example.qukuailian.util.Cert.*;
import com.example.qukuailian.util.Cert.smutil.BCECUtil;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

@Service
public class CertService {

//    public static void main(String[] args) {
//        try {
//            KeyPair issKP = SM2Util.generateKeyPair();
//            ObjectOutputStream oos1 = new ObjectOutputStream(new FileOutputStream("key-pair.out"));
//            oos1.writeObject(issKP);
//            oos1.close();
//        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | IOException e) {
//            e.printStackTrace();
//        }
//    }

    static SM2X509CertMaker sm2X509CertMaker;

    static private KeyPair issuerKp;

    static {
        Security.addProvider(new BouncyCastleProvider());
        Security.removeProvider("SunEC");
        try {
            sm2X509CertMaker = buildCertMaker();
        } catch (Exception e) {
            e.printStackTrace();
            sm2X509CertMaker = null;
        }
    }

    public static boolean verifyCertificate(MultipartFile file) throws Exception {
        CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        X509Certificate x509cert = (X509Certificate)certFactory.generateCertificate(file.getInputStream());
        return SM2CertUtil.verifyCertificate((BCECPublicKey) issuerKp.getPublic(), x509cert);
    }

    /**
     *
     * @param commonName    用户名
     * @param org           组织
     * @param keyPair       密钥对
     * @return 证书的字节码
     * @throws Exception
     */
    public static byte[] GenerateCertificateBySubKP(String commonName, String org, KeyPair keyPair) throws Exception {
        X509Certificate cert = GenerateCertificate("China", "Blockchain Application", org,
                commonName, "",
                keyPair);
        if (cert == null) {
            System.out.println("生成证书失败！");
            return null;
        } else {
            return cert.getEncoded();
        }
    }

    /**
     *
     * @param country 城市名
     * @param orgName 组织部门名
     * @param orgUnit 组织单位名
     * @param commonName 通用名
     * @param emailAddr 电子邮件地址
     */
    public static X509Certificate GenerateCertificate(String country, String orgName, String orgUnit,
                                                      String commonName, String emailAddr, KeyPair subKP) throws Exception {
        // KeyPair subKP = SM2Util.generateKeyPair();
        X500Name subDN = buildSubjectDN(country, orgName, orgUnit, commonName, emailAddr);
        SM2PublicKey sm2SubPub = new SM2PublicKey(subKP.getPublic().getAlgorithm(),
                (BCECPublicKey) subKP.getPublic());
        byte[] csr = CommonUtil.createCSR(subDN, sm2SubPub, subKP.getPrivate(),
                SM2X509CertMaker.SIGN_ALGO_SM3WITHSM2).getEncoded();
        // savePriKey(fileName, (BCECPrivateKey) subKP.getPrivate(),
        //        (BCECPublicKey) subKP.getPublic());
        SM2X509CertMaker certMaker = buildCertMaker();
        // FileUtil.writeFile(fileName, cert.getEncoded());
        return certMaker.makeSSLEndEntityCert(csr);
    }

    public static void savePriKey(String filePath, BCECPrivateKey priKey, BCECPublicKey pubKey) throws IOException {
        ECPrivateKeyParameters priKeyParam = BCECUtil.convertPrivateKeyToParameters(priKey);
        ECPublicKeyParameters pubKeyParam = BCECUtil.convertPublicKeyToParameters(pubKey);
        byte[] derPriKey = BCECUtil.convertECPrivateKeyToSEC1(priKeyParam, pubKeyParam);
        FileUtil.writeFile(filePath, derPriKey);
    }

    public static X500Name buildSubjectDN(String country, String orgName, String orgUnit, String commonName, String emailAddr) {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.C, country);        // country 城市名
        builder.addRDN(BCStyle.O, orgName);        // organizationName 组织部门名
        builder.addRDN(BCStyle.OU, orgUnit);       // OrganizationUnit 组织单位
        builder.addRDN(BCStyle.CN, commonName);    // commonName 通用名
        builder.addRDN(BCStyle.EmailAddress, emailAddr);
        return builder.build();
    }

    public static X500Name buildRootCADN() {
        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.C, "Beijing");           // country 城市名
        builder.addRDN(BCStyle.O, "BUPT");              // organizationName 组织部门名
        builder.addRDN(BCStyle.OU, "Block Chain");      // OrganizationUnit 组织单位
        builder.addRDN(BCStyle.CN, "Privacy Service");  // commonName 通用名
        return builder.build();
    }

    public static SM2X509CertMaker buildCertMaker() throws InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, NoSuchProviderException, InvalidX500NameException, IOException, ClassNotFoundException {
        X500Name issuerName = buildRootCADN();

        ClassPathResource pathResource = new ClassPathResource("issuer/key-pair.out");
        ObjectInputStream ois1 = new ObjectInputStream(pathResource.getInputStream());
        issuerKp = (KeyPair) ois1.readObject();

        long certExpire = 20L * 365 * 24 * 60 * 60 * 1000; // 20年
        CertSNAllocator snAllocator = new RandomSNAllocator(); // 实际应用中可能需要使用数据库来保证证书序列号的唯一性。
        return new SM2X509CertMaker(issuerKp, certExpire, issuerName, snAllocator);
    }
}
