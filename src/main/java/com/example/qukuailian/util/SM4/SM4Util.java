package com.example.qukuailian.util.SM4;

import java.lang.reflect.Method;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;

import com.example.qukuailian.bean.User;
import com.example.qukuailian.util.SMA;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;

/**
 * sm4加密算法工具类
 * @explain sm4加密、解密与加密结果验证
 *          可逆算法
 * @author Marydon
 * @creationTime 2018年7月6日上午11:46:59
 * @version 1.0
 * @since
 * @email marydon20170307@163.com
 */
public class SM4Util {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static final String ENCODING = "UTF-8";
    public static final String ALGORITHM_NAME = "SM4";
    // 加密算法/分组加密模式/分组填充方式
    // PKCS5Padding-以8个字节为一组进行分组加密
    // 定义分组加密模式使用：PKCS5Padding
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
    // 128-32位16进制；256-64位16进制
    public static final int DEFAULT_KEY_SIZE = 128;

    /**
     * 生成ECB暗号
     * @explain ECB模式（电子密码本模式：Electronic codebook）
     * @param algorithmName
     *            算法名称
     * @param mode
     *            模式
     * @param key
     * @return
     * @throws Exception
     */
    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithmName, BouncyCastleProvider.PROVIDER_NAME);
        Key sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        cipher.init(mode, sm4Key);
        return cipher;
    }

    /**
     * 自动生成密钥
     * @explain
     * @return
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public static String generateKey(String id) throws Exception {
        return ByteUtils.toHexString(generateKey(DEFAULT_KEY_SIZE));
    }

    /**
     * @explain
     * @param keySize
     * @return
     * @throws Exception
     */
    public static byte[] generateKey(int keySize) throws Exception {
        KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_NAME, BouncyCastleProvider.PROVIDER_NAME);
        kg.init(keySize, new SecureRandom());
        return kg.generateKey().getEncoded();
    }
    /**
     * sm4加密
     * @explain 加密模式：ECB
     *          密文长度不固定，会随着被加密字符串长度的变化而变化
     * @param key
     *            16进制密钥（忽略大小写）
     * @param text
     *            待加密字符串
     * @return 返回16进制的加密字符串
     * @throws Exception
     */
    public static String encrypt(String text, String key) throws Exception {
        String cipherText = "";
        if(text == null || text.equals("")){
            return "";
        }
        // 16进制字符串-->byte[]
        byte[] keyData = ByteUtils.fromHexString(key);
        // String-->byte[]
        byte[] srcData = text.getBytes(ENCODING);
        // 加密后的数组
        byte[] cipherArray = encrypt_Ecb_Padding(keyData, srcData);
        // byte[]-->hexString
        cipherText = ByteUtils.toHexString(cipherArray);
        return cipherText;
    }

    /**
     * 加密模式之Ecb
     * @explain
     * @param key
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] encrypt_Ecb_Padding(byte[] key, byte[] data) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * sm4解密
     * @explain 解密模式：采用ECB
     * @param key
     *            16进制密钥
     * @param text
     *            16进制的加密字符串（忽略大小写）
     * @return 解密后的字符串
     * @throws Exception
     */
    public static String decrypt(String text, String key) throws Exception {
        // 用于接收解密后的字符串
        String decryptStr = "";
        if(text == null || text.equals("")){
            return "";
        }
        // hexString-->byte[]
        byte[] keyData = ByteUtils.fromHexString(key);
        // hexString-->byte[]
        byte[] cipherData = ByteUtils.fromHexString(text);
        // 解密
        byte[] srcData = decrypt_Ecb_Padding(keyData, cipherData);
        // byte[]-->String
        decryptStr = new String(srcData, ENCODING);
        return decryptStr;
    }

    /**
     * 解密
     * @explain
     * @param key
     * @param cipherText
     * @return
     * @throws Exception
     */
    public static byte[] decrypt_Ecb_Padding(byte[] key, byte[] cipherText) throws Exception {
        Cipher cipher = generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(cipherText);
    }

    /**
     * 校验加密前后的字符串是否为同一数据
     * @explain
     * @param hexKey
     *            16进制密钥（忽略大小写）
     * @param cipherText
     *            16进制加密后的字符串
     * @param paramStr
     *            加密前的字符串
     * @return 是否为同一数据
     * @throws Exception
     */
    public static boolean verifyEcb(String hexKey, String cipherText, String paramStr) throws Exception {
        // 用于接收校验结果
        boolean flag = false;
        // hexString-->byte[]
        byte[] keyData = ByteUtils.fromHexString(hexKey);
        // 将16进制字符串转换成数组
        byte[] cipherData = ByteUtils.fromHexString(cipherText);
        // 解密
        byte[] decryptData = decrypt_Ecb_Padding(keyData, cipherData);
        // 将原字符串转换成byte[]
        byte[] srcData = paramStr.getBytes(ENCODING);
        // 判断2个数组是否一致
        flag = Arrays.equals(decryptData, srcData);
        return flag;
    }

    public static void main(String[] args) {
        try {
            Class<?> clazz = null;
            String algtype = "4";
            String key = generateKey("");
            if(algtype.equals("2")){
                clazz = Class.forName(SMA.SM2.getClassName());
            }else if(algtype.equals("4")){
                clazz = Class.forName(SMA.SM4.getClassName());
            }
            Method method = clazz.getMethod("encrypt", new Class[]{String.class, String.class});
            String data = (String) method.invoke(null, new Object[]{"aa", key});
            System.out.println(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}