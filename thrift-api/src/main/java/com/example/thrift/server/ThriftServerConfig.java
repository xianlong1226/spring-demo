package com.example.thrift.server;

import com.example.thrift.api.UserService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Thrift Server 配置 — Spring Boot 启动时启动 Thrift RPC 服务
 * 使用 TThreadPoolServer（生产级，线程池模式）
 */
@Configuration
public class ThriftServerConfig {

    @Value("${thrift.server.port:9090}")
    private int thriftPort;

    @Value("${thrift.server.min-threads:5}")
    private int minThreads;

    @Value("${thrift.server.max-threads:200}")
    private int maxThreads;

    private TServer server;

    private final UserServiceImpl userServiceImpl;

    public ThriftServerConfig(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostConstruct
    public void start() throws TTransportException {
        UserService.Processor<UserServiceImpl> processor =
            new UserService.Processor<>(userServiceImpl);

        TServerSocket serverTransport = new TServerSocket(thriftPort);
        TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();

        server = new TThreadPoolServer(
            new TThreadPoolServer.Args(serverTransport)
                .processor(processor)
                .protocolFactory(protocolFactory)
                .minWorkerThreads(minThreads)
                .maxWorkerThreads(maxThreads)
        );

        // 异步启动 Thrift Server，不阻塞 Spring Boot 主线程
        new Thread(() -> {
            System.out.println("🚀 Thrift Server 启动，监听端口: " + thriftPort);
            server.serve();
        }, "thrift-server").start();
    }

    @PreDestroy
    public void stop() {
        if (server != null && server.isServing()) {
            System.out.println("🛑 Thrift Server 关闭");
            server.stop();
        }
    }
}
