package com.example.modules.article.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章响应数据 — 返回给前端的数据结构
 * 
 * ⚠️ 不直接返回 Entity，通过 VO 控制暴露哪些字段
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArticleVO {

    private Long id;
    private String title;
    private String content;
    private Long authorId;
    private String authorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
