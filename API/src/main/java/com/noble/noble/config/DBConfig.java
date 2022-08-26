package com.noble.noble.config;

import com.noble.noble.NobleApplication;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackageClasses = NobleApplication.class)
public class DBConfig {
    
}
