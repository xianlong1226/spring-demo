# 项目文件位置约束指南

> 理解"为什么文件必须放在这里"，你就掌握了 Spring Boot 项目的骨架。

## 三层约束总览

```
┌─────────────────────────────────────────────────────┐
│  第 1 层：Gradle（build.gradle）                      │
│  约束：.java 必须在 src/main/java/，.yml 必须在       │
│        src/main/resources/                           │
│  违反后果：文件不会被编译，等于不存在                    │
├─────────────────────────────────────────────────────┤
│  第 2 层：Java 语言（package 声明）                    │
│  约束：package com.example.xxx 必须对应目录             │
│        com/example/xxx/                              │
│  违反后果：编译报错                                    │
├─────────────────────────────────────────────────────┤
│  第 3 层：Spring Boot（@SpringBootApplication）        │
│  约束：组件必须在 Application.java 所在包及其子包下      │
│  违反后果：类存在但不会被 Spring 管理，注解不生效         │
│                                                      │
│  额外：application.yml 中的 mybatis.type-aliases-     │
│  package 约束了 MyBatis 实体类的位置                    │
└─────────────────────────────────────────────────────┘
```

---

## 第 1 层：Gradle 约束 — 源码放哪个目录

`build.gradle` 中的 Java 插件约定了源码目录：

```
src/main/java/       ← 业务代码必须在这下面
src/main/resources/  ← 配置文件必须在这下面
src/test/java/       ← 测试代码必须在这下面
```

这是 Gradle 的 `sourceSets` 默认值。项目中没有显式配置，因为 Java 插件自带默认值，等价于：

```groovy
sourceSets {
    main {
        java.srcDirs = ['src/main/java']
        resources.srcDirs = ['src/main/resources']
    }
    test {
        java.srcDirs = ['src/test/java']
    }
}
```

### 违反后果

把 `.java` 文件放到 `src/main/java/` 之外的目录 → Gradle 不会编译它 → 等于不存在。

---

## 第 2 层：Java 语言约束 — 包名 = 目录路径

每个 `.java` 文件第一行的 `package` 声明必须和目录结构严格对应：

```
文件路径：src/main/java/com/example/modules/user/entity/User.java
包声明：  package com.example.modules.user.entity;    ← 必须一致！
```

这是 **Java 编译器的硬性规则**，不是 Spring 的要求。

### 违反后果

包声明和实际目录不一致 → 编译直接报错。

---

## 第 3 层：Spring Boot 约束 — 哪些类会被扫描到

### `@SpringBootApplication` 的隐式规则

`Application.java` 中的关键代码：

```java
package com.example;          // ← 启动类在 com.example 包下

@SpringBootApplication         // ← 这个注解自带 @ComponentScan
public class Application { ... }
```

`@SpringBootApplication` 暗含了一个关键行为：**只扫描 `com.example` 及其所有子包下的组件**。

| 位置 | 是否被扫描 | 原因 |
|------|-----------|------|
| `com.example.config.SecurityConfig` | ✅ | 是 `com.example` 的子包 |
| `com.example.modules.user.service.UserService` | ✅ | 是 `com.example` 的子包 |
| `com.example.modules.article.controller.ArticleController` | ✅ | 是 `com.example` 的子包 |
| `com.other.project.SomeService` | ❌ | 不在 `com.example` 下，Spring 根本不知道它的存在 |

所有 `@Controller`、`@Service`、`@Repository`、`@Component`、`@Configuration` 等注解能生效，**前提就是类在 `com.example` 包树下**。

### 违反后果

类存在但不在扫描范围内 → Spring 不会创建它的 Bean → 注解不生效 → 运行时报 `NoSuchBeanDefinitionException` 或接口 404。

### `application.yml` 中的额外约束

```yaml
mybatis:
  type-aliases-package: com.example.modules.article.entity
```

这行告诉 MyBatis：**只扫描 `com.example.modules.article.entity` 包下的类作为类型别名**。所以 `Article.java` 必须放在这个包下，否则 MyBatis 找不到它。

---

## 实战：新建一个模块需要怎么做？

比如想加一个"评论模块"，你需要：

1. **创建目录**：在 `src/main/java/com/example/modules/` 下创建 `comment/` 子目录（满足第 1、2、3 层）
2. **包声明**：写 `package com.example.modules.comment.xxx`（满足第 2 层）
3. **放在正确源码目录下**：确保文件在 `src/main/java/` 下（满足第 1 层）

只要遵守这三层，Spring 就能自动发现并管理你的新类，不需要额外配置。

### 完整目录示例

```
src/main/java/com/example/modules/comment/
├── entity/
│   └── Comment.java               package: com.example.modules.comment.entity
├── dto/
│   └── CommentCreateDTO.java      package: com.example.modules.comment.dto
├── vo/
│   └── CommentVO.java             package: com.example.modules.comment.vo
├── mapper/   (如果用 MyBatis)
│   └── CommentMapper.java         package: com.example.modules.comment.mapper
├── repository/   (如果用 JPA)
│   └── CommentRepository.java     package: com.example.modules.comment.repository
├── service/
│   └── CommentService.java        package: com.example.modules.comment.service
└── controller/
    └── CommentController.java     package: com.example.modules.comment.controller
```

---

## 如果要打破默认规则怎么办？

| 需求 | 做法 |
|------|------|
| 源码目录不在 `src/main/java/` | 在 `build.gradle` 中修改 `sourceSets.main.java.srcDirs` |
| 启动类不在根包下 | 在 `@SpringBootApplication` 上加 `scanBasePackages = "com.your.pkg"` |
| 引入第三方包的组件 | 用 `@Import` 或 `@ComponentScan` 显式指定 |
| MyBatis 实体不在默认包 | 修改 `application.yml` 中的 `mybatis.type-aliases-package` |
| JPA 实体不在默认包 | 修改 `@EntityScan` 注解或让启动类扫描到即可 |

> **新手建议**：先遵守默认规则，等理解了再考虑自定义。默认约定足够覆盖绝大多数场景。
