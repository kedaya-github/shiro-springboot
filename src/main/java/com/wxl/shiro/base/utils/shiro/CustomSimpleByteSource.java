package com.wxl.shiro.base.utils.shiro;

import org.apache.shiro.codec.Base64;
import org.apache.shiro.codec.CodecSupport;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.util.ByteSource;

import java.io.Serializable;

/**
 * @author Weixl
 * @date 2021/10/20
 */
public class CustomSimpleByteSource implements ByteSource , Serializable {
    private static final long serialVersionUID = 5528101080905698238L;

    private volatile byte[] bytes;
    private String cachedHex;
    private String cachedBase64;

    public CustomSimpleByteSource() {
    }

    public CustomSimpleByteSource(char[] passWord) {
        this.bytes = CodecSupport.toBytes(passWord);
    }

    @Override
    public byte[] getBytes() {
        return this.bytes;
    }

    @Override
    public boolean isEmpty() {
        return this.bytes == null || this.bytes.length == 0;
    }

    @Override
    public String toHex() {
        if (this.cachedHex == null) {
            this.cachedHex = Hex.encodeToString(this.getBytes());
        }

        return this.cachedHex;
    }

    @Override
    public String toBase64() {
        if (this.cachedBase64 == null) {
            this.cachedBase64 = Base64.encodeToString(this.getBytes());
        }

        return this.cachedBase64;
    }
}