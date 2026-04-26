package com.vague.core.chat.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.vague.core.**.mapper")
public class MybatisPlusConfig {
}
