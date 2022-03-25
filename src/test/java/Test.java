import com.wxl.shiro.base.utils.RsaUtils;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * @author Weixl
 * @date 2021/10/28
 */
public class Test {

    @org.junit.Test
    public void test() throws Exception {
        KeyPair keyPair = RsaUtils.getKeyPair();

        PrivateKey aPrivate = keyPair.getPrivate();
        System.out.println("私钥："+Base64.getEncoder().encodeToString(aPrivate.getEncoded()));
        // 私钥：MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAIG27alBmnHEDJNX3CefIPpHVbezQNH94YQ+QlQOtrZrPeV8Fty4/cT3ijNtixBOkuRIlpi//y7UcqXqTYJuAXkB6ZskgmGyBjUtJG5gfm1n3epYFr3paR2Fs28gf8iT/WdR1sfC4bYaOGzk5kKFmA9szKsuG+KgzcEYv4o51R0VAgMBAAECgYALhfKq/Jb50E7FsCJwqqRFV5z3ysbRYNpt4xIFYaE9p11CS7nENfLlUpBGbU7TTgeinAg03amPwXPF5YCpO5iJxPhFvFBexL/JE59zDJKloRyfMQIRMqjJbASmcqEYIeqd0pdagcN9mdEDBwWVpqIV5fSqn5AyVkpFUyZHBzcWAQJBAMRMqJ7tUuQJIVgrsuBS2NdqJjXHd/icuoqznUXnItSMgZ5f+Dk/hUoli9GKmkCaEP/GDK2YNTd8EQTMTl4F2EECQQCpKibSWem7UgmRla4LZW/9ql1vBybG8EN2NAl3ENTAo6bE6bP2axQsj+ViIJ5Si/9NsY2x5uFzq2fnHfdJNG/VAkEAriTDwpoP2zUuW6YIvnFA5XnKBBO9HtULuFi3wXfXsnAj3XiOIVt0x96fN3mado03X3E3dhl9vIdYIcWOEGNnAQJAU2gWFhcA+DwdvatYUgQksKSkRs723pWuHYTQueURkR3fZy13buMi4kncpoJAraCbM3p8y59zv61eoISU3YI+NQJASXcZrBFlvOvIirbG8O8vr6MQrQYsuH9DYALUnZVPPUzYLCXyGHQSuVAFt4KN3qL5Nh5xtfsUGmcJ5frydOa8Rw==
        PublicKey aPublic = keyPair.getPublic();
        System.out.println("公钥："+Base64.getEncoder().encodeToString(aPublic.getEncoded()));
        // 公钥：MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBtu2pQZpxxAyTV9wnnyD6R1W3s0DR/eGEPkJUDra2az3lfBbcuP3E94ozbYsQTpLkSJaYv/8u1HKl6k2CbgF5AembJIJhsgY1LSRuYH5tZ93qWBa96WkdhbNvIH/Ik/1nUdbHwuG2Gjhs5OZChZgPbMyrLhvioM3BGL+KOdUdFQIDAQAB

        String data = "Aa111111";

        // 私钥加密公钥解密
        String encrypt = RsaUtils.encryptByPrivateKey(data, Base64.getEncoder().encodeToString(aPrivate.getEncoded()));
        System.out.println("私钥加密字符串：" + encrypt);
        // 私钥加密字符串：DbSZHJaIfNytbbPNQq42vKRAcQq2pnkkhtoWRXXUXSGVBhRjyeUXZy+Y/VwPJvmUaIjeIHDVjQ8mvZmX2s2ifH7yaxR8+dKhM9dz7k+1/ydR4C2kWLcTPuAPN1da/PigBkMp5mq+W5mpFaJHzrpGENxvNTdPFhzV3U3IJ2kTTcs=
        String decrypt = RsaUtils.decryptByPublicKey(encrypt, Base64.getEncoder().encodeToString(aPublic.getEncoded()));
        System.out.println("公钥解密字符串：" + decrypt);
        // 私钥解密字符串：Aa111111

        // 公钥加密私钥解密
        String encrypt1 = RsaUtils.encryptByPublicKey(data, Base64.getEncoder().encodeToString(aPublic.getEncoded()));
        System.out.println("公钥加密字符串：" + encrypt1);
        // 公钥加密字符串：YDxMpB90m2LdGi6inP2LKZZ5ck8j//Uexm4qSw7K351d8JNVjjIwj28WkjGTkjmAc5q51h05ApdKjBfgYmEAQY7t4oa0v4xbmgwbuvPIMh4L7ZlcpRc2nRqdDckLRjICrfm/Q9K4kU+jViPXjyVQ4DR9fZvfdJfu7xGg7vbjpbQ=
        String decrypt1 = RsaUtils.decryptByPrivateKey(encrypt1, Base64.getEncoder().encodeToString(aPrivate.getEncoded()));
        System.out.println("私钥解密字符串：" + decrypt1);
        // 公钥解密字符串：Aa111111
    }
}
