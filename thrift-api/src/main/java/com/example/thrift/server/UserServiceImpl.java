package com.example.thrift.server;

import com.example.thrift.api.ThriftUser;
import com.example.thrift.api.CreateThriftUserRequest;
import com.example.thrift.api.ThriftUserResponse;
import com.example.thrift.api.UserNotFoundException;
import com.example.thrift.api.UserService;

import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * UserService RPC 服务端实现 — 内存 Map 模拟数据存储
 */
@Service
public class UserServiceImpl implements UserService.Iface {

    private final Map<Long, ThriftUser> userStore = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    // 初始化一些测试数据
    public UserServiceImpl() {
        ThriftUser user1 = new ThriftUser(1L, "宪龙", "xianlong@example.com", 30);
        ThriftUser user2 = new ThriftUser(2L, "测试用户", "test@example.com", 25);
        userStore.put(user1.id, user1);
        userStore.put(user2.id, user2);
    }

    @Override
    public ThriftUserResponse getUser(long id) throws UserNotFoundException, TException {
        ThriftUser user = userStore.get(id);
        if (user == null) {
            throw new UserNotFoundException("用户不存在: id=" + id);
        }
        return new ThriftUserResponse(user, "查询成功", true);
    }

    @Override
    public ThriftUserResponse createUser(CreateThriftUserRequest req) throws TException {
        long newId = idGenerator.incrementAndGet();
        ThriftUser user = new ThriftUser(newId, req.name, req.email, req.age);
        userStore.put(newId, user);
        return new ThriftUserResponse(user, "创建成功", true);
    }

    @Override
    public ThriftUserResponse listUsers() throws TException {
        List<ThriftUser> users = new ArrayList<>(userStore.values());
        // 用第一个用户作为代表 + message 包含总数
        ThriftUser repUser = users.isEmpty() ? new ThriftUser(0L, "无用户", "", 0) : users.get(0);
        return new ThriftUserResponse(repUser, "共 " + users.size() + " 个用户", true);
    }
}
