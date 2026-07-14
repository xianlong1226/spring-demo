package com.example.modules.thriftclient;

import org.springframework.context.annotation.Configuration;

/**
 * Thrift 客户端配置 — 声明 thriftclient 模块组件
 * Spring Boot 自动扫描 com.example 下的所有 @Component/@Service，
 * 此 Configuration 主要作为模块标记
 */
@Configuration
public class ThriftClientConfig {
    // UserServiceThriftClient 已通过 @Component 自动注册
    // 如需连接池等高级配置，在此扩展
}
