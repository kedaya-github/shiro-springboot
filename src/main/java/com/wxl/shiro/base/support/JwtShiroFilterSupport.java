package com.wxl.shiro.base.support;

import com.wxl.shiro.base.bo.Resource;
import com.wxl.shiro.base.bo.Role;
import com.wxl.shiro.base.configuration.ApplicationBeanUtils;
import com.wxl.shiro.base.mapper.ResourceMapper;
import com.wxl.shiro.base.mapper.RoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.mgt.DefaultFilterChainManager;
import org.apache.shiro.web.filter.mgt.PathMatchingFilterChainResolver;
import org.apache.shiro.web.servlet.AbstractShiroFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Weixl
 * @date 2021/10/25
 */
@Component
@Slf4j
public class JwtShiroFilterSupport {

    @Autowired
    private ResourceMapper resourceMapper;

    @Autowired
    private RoleMapper roleMapper;

    /**
    *  Jwt专用 无状态
    * @return java.util.Map<java.lang.String,java.lang.String>
    */
    public Map<String, String> getJwtFilterChain() {
        HashMap<String, String> listMap = new LinkedHashMap<>();
        // anon默认授权 ， authc登录鉴权
        listMap.put("/static/**" , "anon");
        listMap.put("/user/add" , "anon");
        listMap.put("/manager/login" , "anon");
        // 使用动态过滤器链路 , 从数据库查询 , 只查询 3级(功能授权)和4级(菜单下默认权限)的即可
        List<Resource> resourceList = resourceMapper.queryAll();
        resourceList.forEach(resource -> {
            // 如果是 4 级静默授权就查询父节点的角色 进行过滤器链路
            List<Role> roleList;
            if ("4".equals(resource.getLeaf())) {
                roleList = roleMapper.queryByResourceId(resource.getParentId());
            } else {
                // 查询role角色信息
                roleList = roleMapper.queryByResourceId(resource.getId());
            }
            if (CollectionUtils.isNotEmpty(roleList)) {
                List<String> roleLabelList = roleList.stream().map(Role::getLabel).collect(Collectors.toList());
//                listMap.put(resource.getServiceName() , "customUser,customCheck,customRole" + roleLabelList.toString());
            }
        });
        listMap.put("/**" , "jwtCustomUser");
        return listMap;
    }


    /**
    *  更新url权限过滤配置
    * @return void
    */
    public void updatePathCheck() {
        log.info("更新url过滤器链路===>{}" , System.currentTimeMillis());
        // 更新url过滤配置
        synchronized (this) {
            try {
                // 获取过滤器工厂
                ShiroFilterFactoryBean filterFactoryBean = ApplicationBeanUtils.getBean(ShiroFilterFactoryBean.class);
                // 获取具体的过滤器
                AbstractShiroFilter shiroFilter = (AbstractShiroFilter) filterFactoryBean.getObject();
                // 获取url权限路由匹配的解析器
                PathMatchingFilterChainResolver filterChainResolver = (PathMatchingFilterChainResolver) shiroFilter.getFilterChainResolver();

                // 通过解析器获取拦截器链管理器，管理者 url和filter拦截器的关系
                DefaultFilterChainManager filterChainManager = (DefaultFilterChainManager) filterChainResolver.getFilterChainManager();
                // 清空旧的权限过滤链配置
                filterChainManager.getFilterChains().clear();
                // 清空工厂中配置的FilterChainDefinitionMap
                filterFactoryBean.getFilterChainDefinitionMap().clear();

                // 重新构建过滤器链路信息到 ShiroFilterBean工厂中
                Map<String, String> filterChainMap = getJwtFilterChain();
                filterFactoryBean.setFilterChainDefinitionMap(filterChainMap);

                // 重新构建url过滤器校验 到过滤器管理器中
                for (Map.Entry<String, String> entry : filterChainMap.entrySet()) {
                    log.info("添加过滤器链路,url=>{},chain=>{}" , entry.getKey() , entry.getValue());
                    filterChainManager.createChain(entry.getKey() , entry.getValue().trim().replace(" ",""));
                }
            }catch (Exception e) {
                log.error("更新url过滤器权限报错" , e);
            }
        }
    }
}
