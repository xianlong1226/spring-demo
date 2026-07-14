package com.example.thrift;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * thrift-api 模块启动入口 — Thrift RPC 服务端
 */
@SpringBootApplication
public class ThriftApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThriftApiApplication.class, args);
    }
}
