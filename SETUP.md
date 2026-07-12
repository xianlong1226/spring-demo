# 环境配置与项目运行指南

> 从零开始，把项目跑起来的完整步骤  
> 宪龙 · 2026-07-11

---

## 一、必装软件

### 一键安装（推荐）

```bash
brew install openjdk@17 gradle postgresql@17 redis
```

| # | 软件 | 用途 | 验证命令 |
|---|------|------|----------|
| 1 | JDK 17 | Java 运行环境（LTS 版本） | `java -version` |
| 2 | Gradle | 依赖管理 + 项目构建 | `gradle -version` |
| 3 | PostgreSQL 17 | 数据库 | `psql --version` |
| 4 | Redis | 缓存 | `redis-cli ping` |

> 💡 不需要全局安装 Gradle，项目自带 Gradle Wrapper（`./gradlew`），首次运行会自动下载。

### 环境变量配置

安装完成后，将以下内容添加到 `~/.zshrc`：

```bash
# JDK
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"

# PostgreSQL
export PATH="/opt/homebrew/opt/postgresql@17/bin:$PATH"
```

保存后执行：

```bash
source ~/.zshrc
```

### VSCode 必装扩展

| 扩展 | 作用 |
|------|------|
| **Extension Pack for Java**（微软官方） | Java 开发全家桶：语言支持、调试、构建、测试 |
| **Spring Boot Extension Pack** | Spring 开发支持：自动补全、配置提示 |
| **Lombok Annotations Support** | Lombok 注解识别 |

---

## 二、Maven vs Gradle 速查

本项目使用 Gradle，以下是和 Maven 的对照：

| 操作 | Maven | Gradle |
|------|-------|--------|
| 清理 + 构建 | `mvn clean install` | `./gradlew clean build` |
| 运行项目 | `mvn spring-boot:run` | `./gradlew bootRun` |
| 跳过测试 | `mvn clean install -DskipTests` | `./gradlew clean build -x test` |
| 查看依赖树 | `mvn dependency:tree` | `./gradlew dependencies` |
| 配置文件 | `pom.xml` (XML) | `build.gradle` (Groovy) |
| 本地缓存 | `~/.m2/repository/` | `~/.gradle/caches/` |

---

## 三、启动服务

```bash
# 启动 PostgreSQL
brew services start postgresql@17

# 启动 Redis
brew services start redis
```

---

## 四、数据库配置

### 4.1 创建数据库

```bash
createdb springboot_demo
```

### 4.2 设置密码

密码需要和 `application.yml` 中的 `spring.datasource.password` 一致：

```bash
psql -c "ALTER USER $(whoami) WITH PASSWORD '123456';"
```

### 4.3 建 article 表

MyBatis 不会自动建表，需要手动执行：

```bash
psql -d springboot_demo -c "
CREATE TABLE article (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    content     TEXT,
    author_id   BIGINT REFERENCES sys_user(id),
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);
"
```

> User 表（`sys_user`）无需手动建，JPA 的 `ddl-auto: update` 会自动创建。

---

## 五、项目配置

编辑 `src/main/resources/application.yml`，确认以下配置与你的环境一致：

| 配置项 | 当前值 | 说明 |
|--------|--------|------|
| `spring.datasource.username` | `long` | 改成你的 PG 用户名（终端执行 `whoami` 查看） |
| `spring.datasource.password` | `123456` | 改成你设置的 PG 密码 |
| `spring.datasource.url` | `localhost:5432/springboot_demo` | 一般不用改 |
| `spring.data.redis.host` | `localhost` | 一般不用改 |
| `jwt.secret` | 开发测试值 | 生产环境需更换为强密钥 |

---

## 六、运行项目

```bash
cd ~/workspace/projects/springboot-demo

# 首次运行（会下载 Gradle Wrapper + 依赖，需要几分钟）
./gradlew bootRun

# 如果没有 gradlew，先生成：
gradle wrapper

# 跳过测试
./gradlew bootRun -x test
```

### 验证启动成功

```bash
# 健康检查
curl http://localhost:8080/api/health

# 注册用户
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"***","nickname":"测试用户"}'

# 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"***"}'

# 用返回的 token 访问需要鉴权的接口
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer <your_token>"
```

---

## 七、完整步骤总结

```
1. brew install openjdk@17 gradle postgresql@17 redis
2. 配置环境变量 → source ~/.zshrc
3. brew services start postgresql@17 redis
4. createdb springboot_demo
5. 设置 PG 密码
6. 建 article 表
7. 检查 application.yml 的用户名密码
8. cd 项目目录 && gradle wrapper（首次）
9. ./gradlew bootRun
10. curl localhost:8080/api/health 验证 ✅
```

---

## 八、常见问题

| 问题 | 原因 | 解决 |
|------|------|------|
| `java: command not found` | 环境变量未生效 | 检查 `~/.zshrc` 配置，`source ~/.zshrc` |
| `gradle: command not found` | 未安装 Gradle | `brew install gradle`，或用项目自带的 `./gradlew` |
| 数据库连接失败 | 用户名或密码不对 | 检查 `application.yml` 和 PG 用户名密码 |
| `psql: command not found` | PG 环境变量未配 | 添加 PG 到 PATH |
| `Connection refused: 6379` | Redis 未启动 | `brew services start redis` |
| `article 表不存在` | 忘了手动建表 | 参见 4.3 节 |
| 依赖下载慢 | Gradle 默认源国内慢 | `build.gradle` 已配阿里云镜像 |

### Gradle 阿里云镜像

`build.gradle` 中已配置，无需额外操作：

```groovy
repositories {
    maven { url 'https://maven.aliyun.com/repository/public' }
    maven { url 'https://maven.aliyun.com/repository/spring' }
    mavenCentral()
}
```

---

*本文档由宙斯 🔮 整理 · 2026-07-11*
