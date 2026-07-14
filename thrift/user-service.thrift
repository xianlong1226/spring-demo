namespace java com.example.thrift.api

// 用户数据结构
struct ThriftUser {
    1: i64 id,
    2: string name,
    3: string email,
    4: i32 age
}

struct CreateThriftUserRequest {
    1: string name,
    2: string email,
    3: i32 age
}

struct ThriftUserResponse {
    1: ThriftUser user,
    2: string message,
    3: bool success
}

// 异常
exception UserNotFoundException {
    1: string message
}

// RPC 服务接口
service UserService {
    ThriftUserResponse getUser(1: i64 id) throws (1: UserNotFoundException e),
    ThriftUserResponse createUser(1: CreateThriftUserRequest req),
    ThriftUserResponse listUsers()
}
