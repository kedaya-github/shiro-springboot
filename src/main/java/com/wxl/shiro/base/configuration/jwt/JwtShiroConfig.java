package com.wxl.shiro.base.configuration.jwt;

import com.wxl.shiro.base.configuration.jwt.JwtCustomUserFilter;
import com.wxl.shiro.base.configuration.shiro.UserRealm;
import com.wxl.shiro.base.support.JwtShiroFilterSupport;
import com.wxl.shiro.base.utils.ShiroPathCheckConstant;
import com.wxl.shiro.base.utils.jwt.JwtRealm;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Weixl
 * @date 2021/10/14
 */
@Configuration
@Slf4j
public class JwtShiroConfig {

    @Autowired
    @Lazy
    private JwtShiroFilterSupport shiroFilterSupport;

    /**
     *  Jwt - 无状态 核心权限管理器
     */
    @Bean
    public DefaultWebSecurityManager webSecurityManager(UserRealm userRealm , JwtRealm jwtRealm) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        userRealm.setAuthenticationCachingEnabled(false);
        userRealm.setAuthorizationCachingEnabled(false);
        userRealm.setAuthorizationCacheName(ShiroPathCheckConstant.AUTHORIZATION_CACHE_PREFIX);
        // 2. 设置Jwt-Realm 无状态
        securityManager.setRealm(jwtRealm);
        return securityManager;
    }

    /**
     *  过滤器拦截链路
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        Map<String, String> listMap = shiroFilterSupport.getJwtFilterChain();
        chainDefinition.addPathDefinitions(listMap);
        return chainDefinition;
    }

    /**
     *  过滤器
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(ShiroFilterChainDefinition shiroFilterChainDefinition , DefaultWebSecurityManager securityManager) {
        // 使用默认过滤器
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        shiroFilter.setSecurityManager(securityManager);

        // 自定义过滤器
        shiroFilter.setFilters(getCustomFilter());

        // 设置过滤器拦截链路
        shiroFilter.setFilterChainDefinitionMap(shiroFilterChainDefinition.getFilterChainMap());
        // 设置路径
        shiroFilter.setLoginUrl("/");
        shiroFilter.setSuccessUrl("/");
        shiroFilter.setUnauthorizedUrl("/");
        return shiroFilter;
    }

    /**
     *  自定义Realm
     */
    @Bean
    public JwtRealm jwtRealm() {
        JwtRealm jwtRealm = new JwtRealm();
        // 设置密码校验器
        jwtRealm.setCredentialsMatcher(credentialsMatcher());
        // 设置缓存
        return jwtRealm;
    }

    /**
    *  自定义Realm
    */
    @Bean
    public UserRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        // 设置密码校验器
        userRealm.setCredentialsMatcher(credentialsMatcher());
        // 设置缓存
        return userRealm;
    }

    /**
    *  密码校验器
    */
    private HashedCredentialsMatcher credentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName("MD5");
        //转换次数
        credentialsMatcher.setHashIterations(1);
        return credentialsMatcher;
    }

    /**
     * Jwt - 无状态 封装自定义过滤器
     */
    private Map<String, Filter> getCustomFilter() {
        HashMap<String, Filter> filterList = new LinkedHashMap<>();
        filterList.put("jwtCustomUser" , new JwtCustomUserFilter());
        filterList.put("jwtCustomRole" , new JwtCustomRoleFilter());
        return filterList;
    }
}
