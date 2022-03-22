package com.wxl.shiro.base.configuration.shiro;

import com.wxl.shiro.base.support.ShiroFilterSupport;
import com.wxl.shiro.base.utils.ShiroPathCheckConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;

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
public class ShiroConfig {

    @Autowired
    @Lazy
    private RedisSessionDAO redisSessionDAO;

    @Autowired
    @Lazy
    private RedisCacheManager redisCacheManager;

    @Autowired
    @Lazy
    private StringRedisTemplate redisTemplate;

    @Autowired
    @Lazy
    private ShiroFilterSupport shiroFilterSupport;

    /**
    *  会话管理器
    */
    @Bean
    public DefaultWebSessionManager getDefaultWebSessionManager() {
        // 使用自定义的WebSessionManager，重写了getSessionId方法, 通过requestHeader中 Token来获取
        DefaultWebSessionManager defaultWebSessionManager = new CustomWebSessionManager();
        // 会话过期时间，单位：毫秒(在无操作时开始计时)
        defaultWebSessionManager.setGlobalSessionTimeout(60 * 60 * 1000);
        // 开启自动校验会话过期定时 - 对性能有影响
        defaultWebSessionManager.setSessionValidationSchedulerEnabled(true);
        // 设置session存储
        defaultWebSessionManager.setSessionDAO(redisSessionDAO);
        // 禁用Cookie获取Session
        defaultWebSessionManager.setSessionIdCookieEnabled(false);
        defaultWebSessionManager.setSessionIdUrlRewritingEnabled(false);
//        defaultWebSessionManager.setSessionIdCookieEnabled(true);
        return defaultWebSessionManager;
    }

    /**
     *  核心权限管理器
     */
    @Bean
    public DefaultWebSecurityManager webSecurityManager(UserRealm userRealm , CookieRememberMeManager rememberMeManager) {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        // Realm配置缓存
        // Authorization授权是默认开启 ， Authentication认证是默认关闭的
        userRealm.setAuthenticationCachingEnabled(false);
//        userRealm.setAuthenticationCacheName("test:AuthenticationCache");
//        userRealm.setAuthorizationCachingEnabled(false);
        userRealm.setAuthorizationCacheName(ShiroPathCheckConstant.AUTHORIZATION_CACHE_PREFIX);
        // 设置realm
        securityManager.setRealm(userRealm);
        // 缓存管理器
        securityManager.setCacheManager(redisCacheManager);
        // 会话session管理
        securityManager.setSessionManager(getDefaultWebSessionManager());
        // 记住我功能 ,
//        securityManager.setRememberMeManager(rememberMeManager);
        return securityManager;
    }

    /**
     *  过滤器拦截链路
     */
    @Bean
    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        Map<String, String> listMap = shiroFilterSupport.getFilterChain();
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
    public UserRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        // 设置密码校验器
        userRealm.setCredentialsMatcher(credentialsMatcher());
        // 设置缓存
        return userRealm;
    }

    /**
    *  记住我登录管理器
     *  一般不要在后端实现自动登录的功能,会有分布式共享数据的问题，
     *  如果要实现记住我自动登录，则前端（默认）判断登陆失效 就默认走正常的登录流程
    * @return CookieRememberMeManager
    */
    @Bean
    public CookieRememberMeManager cookieRememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        SimpleCookie simpleCookie = new SimpleCookie();
        // 设置过期时间7天
        simpleCookie.setMaxAge(60 * 60 * 24 * 7);
        // 设置cookie的key名称
        simpleCookie.setName("rememberMe");
        // 设置仅http传输
        simpleCookie.setHttpOnly(false);
        simpleCookie.setPath("/");
        cookieRememberMeManager.setCookie(simpleCookie);
        //这个地方有点坑，不是所有的base64编码都可以用，长度过大过小都不行，没搞明白，官网给出的要么0x开头十六进制，要么base64
//        cookieRememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return cookieRememberMeManager;
    }

    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     */
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(
//            DefaultWebSecurityManager securityManager) {
//        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
//                new AuthorizationAttributeSourceAdvisor();
//        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
//        return authorizationAttributeSourceAdvisor;
//    }

    /**
     * 管理shiro bean生命周期 , 让Shiro 的配置类可以读取Spring 的bean
     */
//    @Bean
//    public static LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
//        return new LifecycleBeanPostProcessor();
//    }

    /**
     * 如果配置了注解形式，并且在Config中配置了过滤器，就会重复执行认证 doGetAuthorizationInfo 流程
    *  注解方式鉴权生效器 ， 扫描spring上下文将符合的bean配置到切入点中
    */
//    @Bean
//    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
//        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
////        defaultAdvisorAutoProxyCreator.setProxyTargetClass(true);
//        return defaultAdvisorAutoProxyCreator;
//    }
//
//    @Bean
//    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(DefaultWebSecurityManager securityManager) {
//        AuthorizationAttributeSourceAdvisor aas = new AuthorizationAttributeSourceAdvisor();
//        aas.setSecurityManager(securityManager);
//        return aas;
//    }

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
    * 封装自定义过滤器
    */
    private Map<String, Filter> getCustomFilter() {
        HashMap<String, Filter> filterList = new LinkedHashMap<>();
        filterList.put("customLoginNum" , new CustomLoginNumFilter(redisTemplate , redisSessionDAO , getDefaultWebSessionManager()));
        filterList.put("customUser" , new CustomUserFilter());
        filterList.put("customCheck" , new CustomPathCheckFilter());
        filterList.put("customRole" , new CustomRoleFilter());
        return filterList;
    }
}
