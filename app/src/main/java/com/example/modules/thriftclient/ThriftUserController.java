package com.example.modules.thriftclient;

import com.example.thrift.api.ThriftUserResponse;
import com.example.common.result.Result;
import org.springframework.web.bind.annotation.*;

/**
 * Thrift RPC 调用接口 — 通过 REST API 间接调用 thrift-api 模块的 RPC 服务
 * 
 * 流程：前端 → HTTP → app(Controller) → Thrift RPC → thrift-api(Service)
 */
@RestController
@RequestMapping("/api/thrift/users")
public class ThriftUserController {

    private final UserServiceThriftClient thriftClient;

    public ThriftUserController(UserServiceThriftClient thriftClient) {
        this.thriftClient = thriftClient;
    }

    /**
     * 通过 RPC 获取用户
     * GET /api/thrift/users/{id}
     */
    @GetMapping("/{id}")
    public Result<ThriftUserResponse> getUser(@PathVariable long id) {
        ThriftUserResponse response = thriftClient.getUser(id);
        return Result.success(response);
    }

    /**
     * 通过 RPC 创建用户
     * POST /api/thrift/users?name=xxx&email=xxx&age=xxx
     */
    @PostMapping
    public Result<ThriftUserResponse> createUser(@RequestParam String name,
                                                  @RequestParam String email,
                                                  @RequestParam int age) {
        ThriftUserResponse response = thriftClient.createUser(name, email, age);
        return Result.success(response);
    }

    /**
     * 通过 RPC 获取用户列表
     * GET /api/thrift/users/list
     */
    @GetMapping("/list")
    public Result<ThriftUserResponse> listUsers() {
        ThriftUserResponse response = thriftClient.listUsers();
        return Result.success(response);
    }
}
