package com.vague.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")              //所有接口
                .allowedOrigins("*")                    // 允许所有来源（包括本地文件）
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")                    // 允许所有头
                .allowCredentials(false)                // 不携带 cookie
                .maxAge(3600);                          // 预检缓存 1 小时
    }
}
