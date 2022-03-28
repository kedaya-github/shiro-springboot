package com.wxl.shiro.base.utils.jwt;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Weixl
 * @date 2022/3/23
 * jwt工具，用来生成、校验token以及提取token中的信息
 */
@Slf4j
public class JwtUtils {

    private JwtUtils() {
    }

    //指定一个token过期时间（毫秒） 一小时
    private static final long EXPIRE_TIME = 60 * 60 * 1000;

    /**
     * 生成token
     * 注意这里的sercet不是密码，而是进行三件套（salt+MD5+1024Hash）处理密码后得到的凭证
     * 这里为什么要这么做，在controller中进行说明
     * secret 是进行MD5哈希处理后的密令
     */
    public static String getJwtToken(String username , String password , String secret , List<String> roleLabelList) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(secret);    //使用密钥进行哈希
        // 附带用户校验信息 用户名 和 密码 的Token
        return JWT.create()
                .withClaim("username", username)
                .withClaim("password", password)
                .withClaim("role", JSONObject.toJSONString(roleLabelList))
                .withExpiresAt(date)  //过期时间
                .sign(algorithm);     //签名算法
    }

    /**
     * 校验token是否正确 , 密码
     * secret 是 存在数据库中 进行过MD5哈希处理后的密令
     **/
    public static boolean verifyToken(String token, String username, String password, String secret, String roleList) {
        try {
            //根据密钥生成JWT效验器
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withClaim("username", username)
                    .withClaim("password", password)
                    .withClaim("role", roleList)
                    .build();
            //效验TOKEN（其实也就是比较两个token是否相同）
            DecodedJWT jwt = verifier.verify(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 在token中获取到 存储信息
     * @return
     */
    public static Map<String, Claim> getClaims(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaims();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * 判断是否过期
     */
    public static boolean isExpire(String token){
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().getTime() < System.currentTimeMillis() ;
    }
}
