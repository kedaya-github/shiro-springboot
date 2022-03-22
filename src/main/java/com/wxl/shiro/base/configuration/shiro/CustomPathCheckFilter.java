package com.wxl.shiro.base.configuration.shiro;

import com.wxl.shiro.base.configuration.ApplicationBeanUtils;
import com.wxl.shiro.base.support.ShiroFilterSupport;
import com.wxl.shiro.base.utils.ShiroPathCheckConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.PathMatchingFilter;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author Weixl
 * @date 2021/10/19
 */
@Slf4j
public class CustomPathCheckFilter extends PathMatchingFilter {

    private String flag = "success";

    @Override
    protected boolean onPreHandle(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        // 将此校验过滤器，在链路中优先级提高，先进行过滤器链路的动态更新，在往下走下面的过滤器角色校验
        // 每次判断redis存入的更新url过滤器链路的 比较值进行判断,如果更新了与当前 flag 变量不同就更新
        log.info("更新权限判断流程....flag===>{}" , flag);
        String time = ApplicationBeanUtils.getBean(StringRedisTemplate.class).opsForValue().get(ShiroPathCheckConstant.PATH_REDIS_PREFIX);
        if (!flag.equals(time) && !StringUtils.isEmpty(time)) {
            ShiroFilterSupport support = ApplicationBeanUtils.getBean(ShiroFilterSupport.class);
            try {
                support.updatePathCheck();
                flag = time;
            }catch (Exception e) {
                log.error("更新权限过滤失败" , e);
            }
        }
        return true;
    }
}
