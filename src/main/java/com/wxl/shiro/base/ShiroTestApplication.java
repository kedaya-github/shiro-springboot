package com.wxl.shiro.base;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author Weixl
 * @date 2021/10/14
 */
@SpringBootApplication(scanBasePackages = {"com.wxl.shiro.base"})
@MapperScan(basePackages = "com.wxl.shiro.base.mapper")
public class ShiroTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShiroTestApplication.class);
    }

        @Bean
            public CorsFilter corsFilter() {
                //1.添加CORS配置信息
                CorsConfiguration config = new CorsConfiguration();
                //放行哪些原始域
                config.addAllowedOrigin("*");
                //是否发送Cookie信息
                config.setAllowCredentials(true);
                //放行哪些原始域(请求方式)
                config.addAllowedMethod("OPTIONS");
                config.addAllowedMethod("HEAD");
                config.addAllowedMethod("GET");     //get
                config.addAllowedMethod("PUT");     //putw
                config.addAllowedMethod("POST");    //post
                config.addAllowedMethod("DELETE");  //delete
                config.addAllowedMethod("PATCH");
                config.addAllowedHeader("*");

                //2.添加映射路径
                UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
                configSource.registerCorsConfiguration("/**", config);

                //3.返回新的CorsFilter.
                return new CorsFilter(configSource);
            }

}
