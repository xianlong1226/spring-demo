package com.example.modules.thriftclient;

import com.example.thrift.api.CreateThriftUserRequest;
import com.example.thrift.api.ThriftUserResponse;
import com.example.thrift.api.UserService;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Thrift RPC 客户端 — 通过 TSocket 连接 thrift-api 模块调用 UserService
 */
@Component
public class UserServiceThriftClient {

    @Value("${thrift.client.host:localhost}")
    private String host;

    @Value("${thrift.client.port:9090}")
    private int port;

    @Value("${thrift.client.timeout-ms:5000}")
    private int timeoutMs;

    /**
     * 通过 RPC 获取用户
     */
    public ThriftUserResponse getUser(long id) {
        try (TTransport transport = new TSocket(host, port, timeoutMs)) {
            transport.open();
            UserService.Client client = new UserService.Client(
                new TBinaryProtocol(transport)
            );
            return client.getUser(id);
        } catch (Exception e) {
            throw new RuntimeException("RPC 调用 getUser 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 通过 RPC 创建用户
     */
    public ThriftUserResponse createUser(String name, String email, int age) {
        try (TTransport transport = new TSocket(host, port, timeoutMs)) {
            transport.open();
            UserService.Client client = new UserService.Client(
                new TBinaryProtocol(transport)
            );
            CreateThriftUserRequest req = new CreateThriftUserRequest(name, email, age);
            return client.createUser(req);
        } catch (Exception e) {
            throw new RuntimeException("RPC 调用 createUser 失败: " + e.getMessage(), e);
        }
    }

    /**
     * 通过 RPC 获取用户列表
     */
    public ThriftUserResponse listUsers() {
        try (TTransport transport = new TSocket(host, port, timeoutMs)) {
            transport.open();
            UserService.Client client = new UserService.Client(
                new TBinaryProtocol(transport)
            );
            return client.listUsers();
        } catch (Exception e) {
            throw new RuntimeException("RPC 调用 listUsers 失败: " + e.getMessage(), e);
        }
    }
}
