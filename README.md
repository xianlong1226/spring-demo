# Spring Boot Demo

> 前端转全栈练习项目 — 从零跑通一个完整的用户 CRUD + JWT 鉴权

## 项目结构

```
springboot-demo/
├── build.gradle                    # Gradle 依赖配置（类比 package.json）
├── settings.gradle                 # Gradle 项目设置
├── src/main/
│   ├── resources/
│   │   └── application.yml          # 项目配置文件（类比 .env + config）
│   └── java/com/example/
│       ├── Application.java          # 启动入口
│       ├── common/                   # 通用组件
│       │   ├── aspect/
│       │   │   └── LogAspect.java      # AOP 日志切面（自动记录 Service 调用）
│       │   ├── result/
│       │   │   ├── Result.java       # 统一响应结构
│       │   │   └── PageResult.java   # 分页响应结构
│       │   └── exception/
│       │       ├── BusinessException.java       # 业务异常
│       │       └── GlobalExceptionHandler.java  # 全局异常处理
│       ├── config/                   # 配置类
│       │   ├── SecurityConfig.java   # Spring Security + JWT
│       │   └── RedisConfig.java      # Redis 序列化
│       ├── security/                 # 安全相关
│       │   ├── JwtUtil.java          # JWT 工具类
│       │   └── JwtAuthFilter.java    # JWT 认证过滤器
│       └── modules/                  # 业务模块（按模块组织）
│           ├── user/                  # 用户模块（JPA 实现）
│           │   ├── entity/User.java   # 实体（对应数据库表）
│           │   ├── dto/               # 接收前端参数
│           │   │   ├── RegisterDTO.java
│           │   │   ├── LoginDTO.java
│           │   │   └── UserUpdateDTO.java
│           │   ├── vo/                # 返回给前端的数据
│           │   │   ├── UserVO.java
│           │   │   └── LoginVO.java
│           │   ├── repository/        # 数据访问层
│           │   │   └── UserRepository.java
│           │   ├── service/           # 业务逻辑层
│           │   │   └── UserService.java
│           │   └── controller/        # 接口层
│           │       ├── UserController.java
│           │       └── HealthController.java
│           └── article/               # 文章模块（MyBatis 实现，和 user 对比）
│               ├── entity/Article.java
│               ├── dto/
│               │   ├── ArticleCreateDTO.java
│               │   └── ArticleUpdateDTO.java
│               ├── vo/ArticleVO.java
│               ├── mapper/ArticleMapper.java  # MyBatis Mapper（注解模式）
│               ├── service/ArticleService.java
│               └── controller/ArticleController.java
```

## 核心流程：一个请求怎么走通的？

以"获取文章列表"为例，理解数据流向：

```
用户请求 → Controller（接收请求，定义 URL 路由）
              ↓
         Service（业务逻辑，核心代码在这）
              ↓
         Mapper / Repository（操作数据库）
              ↓
         数据库（PostgreSQL）
              ↓
         返回 VO → 统一包装成 Result<T> → JSON 响应给前端
```

> **类比前端**：Controller ≈ Express 路由，Service ≈ 业务逻辑层，Repository/Mapper ≈ API Service

## 文件详解

### 🚀 入口

| 文件 | 作用 |
|------|------|
| `Application.java` | 程序入口，`@SpringBootApplication` 注解启动整个 Spring 容器 |

### 🛠️ 公共层（common）— 所有模块共用

| 文件 | 作用 | 前端类比 |
|------|------|----------|
| `Result.java` | 统一 API 返回格式 `{code, message, data}` | `ApiResponse<T>` 类型 |
| `PageResult.java` | 分页返回格式 `{list, total, page, pageSize}` | 分页数据结构 |
| `BusinessException.java` | 自定义业务异常（如"用户名已存在"） | `throw new Error('xxx')` |
| `GlobalExceptionHandler.java` | 全局异常拦截，把异常转成统一 JSON | 全局错误拦截器 |
| `LogAspect.java` | AOP 自动日志，记录每个 Service 方法的入参/出参/耗时 | 请求拦截器/中间件 |

### ⚙️ 配置层（config + security）

| 文件 | 作用 |
|------|------|
| `SecurityConfig.java` | Spring Security 配置：哪些接口需要登录、哪些公开、JWT 过滤器链 |
| `RedisConfig.java` | Redis 序列化配置（让 `redis-cli` 能看懂存的值） |
| `JwtUtil.java` | JWT 工具类：生成 token、解析 token、校验过期 |
| `JwtAuthFilter.java` | JWT 过滤器：每个请求来了先检查 token 是否合法 |

### 📂 用户模块（JPA 风格）— Spring 帮你自动生成 SQL

| 文件 | 作用 | 前端类比 |
|------|------|----------|
| `User.java` | 用户实体，对应数据库 `sys_user` 表 | TypeScript 类型定义 |
| `UserRepository.java` | 数据访问层，继承 `JpaRepository`，方法名自动生成 SQL | 调 API 的 service 函数 |
| `UserService.java` | 业务逻辑：注册、登录、增删改查 | 业务逻辑层 |
| `UserController.java` | 接口层：定义 URL 路由和请求方法 | 路由 + controller |
| `HealthController.java` | 健康检查接口（供运维/网关探测服务是否存活） | — |

### 📂 文章模块（MyBatis 风格）— 自己写 SQL

| 文件 | 作用 | 与 JPA 的区别 |
|------|------|---------------|
| `Article.java` | 文章实体（纯 POJO，无 JPA 注解） | JPA 用 `@Entity` 等注解，MyBatis 不需要 |
| `ArticleMapper.java` | 用注解写 SQL：`@Select`、`@Insert` 等 | JPA Repository 自动生成 SQL，这里手写 |
| `ArticleService.java` | 文章业务逻辑 | — |
| `ArticleController.java` | 文章接口 | — |

### DTO vs VO（新手必知）

| 类型 | 方向 | 作用 | 本项目示例 |
|------|------|------|-----------|
| **DTO**（Data Transfer Object） | 前端 → 后端 | 接收前端传来的数据 | `RegisterDTO`、`LoginDTO`、`ArticleCreateDTO` |
| **VO**（View Object） | 后端 → 前端 | 返回给前端的数据（过滤掉密码等敏感字段） | `UserVO`（无密码）、`LoginVO`（含 token） |

> 简单记：**DTO 是入口安检（只放行需要的数据），VO 是出口包装（只展示该看的数据）**

### 📄 配置文件

`application.yml` 是整个项目的大脑，管着：

- **端口号**（8080）、**热重载**开关
- **PostgreSQL 数据库**连接地址、账号密码
- **Redis** 连接地址
- **JWT** 密钥和过期时间
- **JPA** 和 **MyBatis** 各自的设置

## 前置条件

- JDK 17+
- Gradle 8.x+（或使用项目自带的 Gradle Wrapper）
- PostgreSQL 17（运行中）
- Redis（运行中）

## 快速开始

### 1. 创建数据库

```bash
psql -c "CREATE DATABASE springboot_demo;"
```

### 2. 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/springboot_demo
    username: your_username    # 改成你的 PG 用户名
    password: your_password    # 改成你的 PG 密码
```

### 3. 运行项目

```bash
# 方式一：Gradle 命令
./gradlew bootRun

# 如果没有 gradlew，先生成：
gradle wrapper

# 方式二：VSCode 中直接运行 Application.java
```

### 4. 验证

```bash
# 健康检查
curl http://localhost:8080/api/health

# 注册
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456","nickname":"测试用户"}'

# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123456"}'

# 用返回的 token 访问需要鉴权的接口
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <your_token>"
```

## 技术栈对比：JPA vs MyBatis

本项目同时包含两种数据访问方式，方便对比学习：

| 维度 | User 模块 (JPA) | Article 模块 (MyBatis) |
|------|-----------------|----------------------|
| 数据访问 | `UserRepository extends JpaRepository` | `ArticleMapper` + `@Select/@Insert` |
| 查询方式 | 方法名自动生成 SQL | 手写 SQL |
| 分页 | `PageRequest.of()` | `PageHelper.startPage()` |
| 实体映射 | `@Entity @Table @Column` | 纯 POJO + yml 驼峰配置 |
| 表自动建 | ✅ `ddl-auto: update` | ❌ 需手动建表 |

## API 接口

### 公开接口（无需登录）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/register | 注册 |
| POST | /api/auth/login | 登录 |
| GET | /api/health | 健康检查 |
| GET | /api/articles | 文章列表（分页） |
| GET | /api/articles/{id} | 文章详情 |
| POST | /api/articles | 创建文章（登录后自动填充作者） |

### 需要登录的接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/users/me | 获取当前用户信息 |
| GET | /api/users/{id} | 获取用户详情 |
| GET | /api/users?page=1&size=10 | 用户列表（分页） |
| PUT | /api/users/{id} | 更新用户信息 |
| DELETE | /api/users/{id} | 删除用户 |
| PUT | /api/articles/{id} | 更新文章 |
| DELETE | /api/articles/{id} | 删除文章 |

## 关键概念速查（前端视角）

| Java 概念 | 前端类比 | 说明 |
|-----------|----------|------|
| Entity | 数据库表结构 | 对应一张表 |
| DTO | 表单提交数据 | 接收前端传入参数 |
| VO | API 响应数据 | 返回给前端的数据 |
| Repository | API Service | 封装数据库操作 |
| Service | 业务逻辑 | 核心代码在这 |
| Controller | Express 路由 | 接收请求、返回响应 |
| @Transactional | 数据库事务 | 保证操作原子性 |
| @Autowired | 依赖注入 | 自动获取需要的 Bean |

## MyBatis 建表

MyBatis 不会自动建表，需要手动执行：

```sql
CREATE TABLE article (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    content     TEXT,
    author_id   BIGINT REFERENCES sys_user(id),
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);
```

## 新手学习路线

```
第 1 步：先跑起来
  → 读 SETUP.md，装好环境，./gradlew bootRun 启动项目

第 2 步：理解一个最简请求
  → 从 HealthController 开始，只有 1 行代码，理解 Controller → 响应

第 3 步：跟踪一个完整流程
  → 用 Postman 调"注册"接口，跟踪：RegisterDTO → UserController → UserService → UserRepository → 数据库

第 4 步：对比两种数据库操作
  → UserRepository（JPA 自动 SQL）vs ArticleMapper（MyBatis 手写 SQL）

第 5 步：理解安全机制
  → 登录拿 token → 带着 token 访问受保护接口 → JwtAuthFilter 校验

第 6 步：理解公共能力
  → Result 统一返回、GlobalExceptionHandler 统一异常、LogAspect 自动日志

第 7 步：动手练习
  → 尝试给 Article 新增一个按标题搜索的功能（MyBatis 动态 SQL）
  → 修改 application.yml 感受配置的变化（如改端口号）
```
