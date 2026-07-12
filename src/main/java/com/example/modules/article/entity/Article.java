package com.example.modules.article.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章实体 — 对应数据库 article 表
 * 
 * ⚠️ 和 User 模块不同，这个用 MyBatis 而不是 JPA
 * 对比：JPA 用注解映射，MyBatis 用 SQL 映射
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Article {

    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
