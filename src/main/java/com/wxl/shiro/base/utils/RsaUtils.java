package com.wxl.shiro.base.utils;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author Weixl
 * @date 2019/9/2
 */
public class RsaUtils {

    private RsaUtils() {
    }

    /**
     * 缺省的1024位密钥对,可处理245个字节(81个汉字)的数据
     */
    private static String PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKZp5rI0inXJfqKYYQ8ikcQRA45W3Px1qF+7mBE6flqSD6yjtkkOSwBqERDSyveRR7lOI3uG85XYGUi5d8I0cZst8uDH7xQVnatf9S+fPeFrdRUom44v+sHL7DjRlb/FbdBzgUcRyRE9OmwqkiRVjJmbNb7MAtTul4rxf1J5bEsvAgMBAAECgYAEnxRFBqiBtOLLaSfo37DknQRYQ5lHeYz/e5oxgEpaAYxl4vYA+WCj1k47BV+VmHdBciYSHCFPm/y3xE7LGeG9vduQqOU+cxxpnYLmU3kdVV+Nx2y+KRJjLlapGZHMip48K8zPgqpRicv7rySBDqgheVJIfyot/rcYJKR6gjoOYQJBAPSux+lUFodk8nFnuve9wdijZOOTZ1Cr5Wo29nHnqStlnPiVuA+5gtDrLpCYjWPLrXJO3Q+D8DC7NZDlr0NPjNMCQQCuHFvCNK68hJOG9HDx2OgVye5NregqD89WwkdntjsdTHNVpeN6NTly2ODjp2qdMN5zxE4fJLVaruujd2+hfx61AkAg28vq8Kjp28uXISgBlxEOJ7snuKbaNYtkfScRWtvkAQxYcbq7Gd2QctTZBDOQoEyVmjOjnsNFuA5UfBPSRT+TAkAU1L3BuY3ibjY8YkiVFu+Fk9hYIONzDNswtSPX67yc38W4zJUvzOhnrj0R37jov60NysHSBhFQnTcCIssPVmc1AkBg2JKjZZzrcwwZZexPXFZKg6hMtF/XNt5URebaELfVat7XWZ2n3d98dThBYqKlfhQ6/rsCrEf50J871LfHDe/B";

    private static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCmaeayNIp1yX6imGEPIpHEEQOOVtz8dahfu5gROn5akg+so7ZJDksAahEQ0sr3kUe5TiN7hvOV2BlIuXfCNHGbLfLgx+8UFZ2rX/Uvnz3ha3UVKJuOL/rBy+w40ZW/xW3Qc4FHEckRPTpsKpIkVYyZmzW+zALU7peK8X9SeWxLLwIDAQAB";

    /**
     * 字符集
     */
    private static String CHARSET = "utf-8";

    /**
     * 签名算法
     */
    private static final String SIGNATURE_INSTANCE = "SHA1WithRSA";

    /**
     * 非对称加密密钥算法
     */
    private static final String KEY_ALGORITHM_RSA = "RSA";

    /**
     * RSA密钥长度
     * 默认1024位，
     * 密钥长度必须是64的倍数，
     * 范围在512至65536位之间。
     */
    private static final int KEY_SIZE = 1024;

    /**
     * 生成密钥对
     *
     * @param keysize 1
     * @return KeyPair
     */
    public static KeyPair getKeyPair(int keysize) throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA);
        keyPairGenerator.initialize(keysize);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 生成密钥对
     * @return KeyPair
     */
    public static KeyPair getKeyPair() throws Exception {
        return getKeyPair(KEY_SIZE);
    }

    /**
     * 生成密钥对
     *
     * @param seed 1
     * @return KeyPair
     */
    public static KeyPair getKeyPair(byte[] seed) throws Exception {

        // 实例化密钥对生成器
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM_RSA);

        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom(seed) );
        // 生成密钥对
        return keyPairGen.generateKeyPair();
    }
    /**
     * 生成密钥对
     *
     * @param seed 1
     * @return KeyPair
     */
    public static KeyPair getKeyPair(String seed) throws Exception {
        return getKeyPair(seed.getBytes(CHARSET));
    }


    /**
     * 公钥字符串转PublicKey实例
     *
     * @param publicKey 1
     * @return PublicKey
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey.getBytes(CHARSET));
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 私钥字符串转PrivateKey实例
     * @param privateKey 1
     * @return PrivateKey
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey.getBytes(CHARSET));
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 公钥加密
     *
     * @param content 1
     * @param publicKey 1
     * @return  byte
     */
    public static byte[] encryptByPublicKey(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }

    public static byte[] encryptByPublicKey(byte[] content) throws Exception {
        return encryptByPublicKey(content, getPublicKey(PUBLIC_KEY));
    }

    public static String encryptByPublicKey(String content, String publicKey) throws Exception {

        byte[] encode = Base64.getEncoder().encode(encryptByPublicKey(content.getBytes(CHARSET), getPublicKey(publicKey)));

        return new String(encode, CHARSET);

    }

    public static String encryptByPublicKey(String content) throws Exception {
        return new String(Base64.getEncoder().encode(encryptByPublicKey(content.getBytes(CHARSET))), CHARSET);
    }

    /**
     * 私钥解密
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }

    public static byte[] decryptByPrivateKey(byte[] content) throws Exception {
        return decryptByPrivateKey(content, getPrivateKey(PRIVATE_KEY));
    }

    public static String decryptByPrivateKey(String content, String privateKey) throws Exception {
        return new String(decryptByPrivateKey(Base64.getDecoder().decode(content), getPrivateKey(privateKey)), CHARSET);

    }

    public static String decryptByPrivateKey(String content) throws Exception {
        return new String(decryptByPrivateKey(Base64.getDecoder().decode(content)), CHARSET);
    }

    /**
     * 私钥加密
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] content, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(content);
    }

    public static byte[] encryptByPrivateKey(byte[] content) throws Exception {
        return encryptByPrivateKey(content, getPrivateKey(PRIVATE_KEY));
    }

    public static String encryptByPrivateKey(String content, String privateKey) throws Exception {
        return Base64.getEncoder().encodeToString(encryptByPrivateKey(content.getBytes(CHARSET), getPrivateKey(privateKey)));
    }

    public static String encryptByPrivateKey(String content) throws Exception {
        return Base64.getEncoder().encodeToString(encryptByPrivateKey(content.getBytes(CHARSET)));
    }

    /**
     * 公钥解密
     *
     * @param content
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] content, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_RSA);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(content);
    }

    public static byte[] decryptByPublicKey(byte[] content) throws Exception {
        return decryptByPublicKey(content, getPublicKey(PUBLIC_KEY));
    }

    public static String decryptByPublicKey(String content, String publicKey) throws Exception {
        return new String(decryptByPublicKey(Base64.getDecoder().decode(content), getPublicKey(publicKey)), CHARSET);

    }

    public static String decryptByPublicKey(String content) throws Exception {
        return new String(decryptByPublicKey(Base64.getDecoder().decode(content)),CHARSET);
    }

    /**
     * 签名
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception
     */
    public static byte[] sign(byte[] content, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_INSTANCE);
        signature.initSign(privateKey);
        signature.update(content);
        return signature.sign();
    }

    public static byte[] sign(byte[] content) throws Exception {
        return sign(content, getPrivateKey(PRIVATE_KEY));
    }

    public static String sign(String content, String privateKey) throws Exception {
        return new String(Base64.getEncoder().encode(sign(content.getBytes(CHARSET), getPrivateKey(privateKey))), CHARSET);
    }

    public static String sign(String content) throws Exception {
        return new String(Base64.getEncoder().encode(sign(content.getBytes(CHARSET))), CHARSET);
    }

    /**
     * 验签
     *
     * @param content
     * @param sign
     * @param publicKey
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] content, byte[] sign, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance(SIGNATURE_INSTANCE);
        signature.initVerify(publicKey);
        signature.update(content);
        return signature.verify(sign);
    }

    public static boolean verify(byte[] content, byte[] sign) throws Exception {
        return verify(content, sign, getPublicKey(PUBLIC_KEY));
    }

    public static boolean verify(String content, String sign, String publicKey) throws Exception {
        return verify(content.getBytes(CHARSET), Base64.getDecoder().decode(sign), getPublicKey(publicKey));
    }

    public static boolean verify(String content, String sign) throws Exception {
        return verify(content.getBytes(CHARSET), Base64.getDecoder().decode(sign), getPublicKey(PUBLIC_KEY));
    }
}


