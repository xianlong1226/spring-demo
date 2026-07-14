package com.example.modules.article.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建文章请求参数
 */
@Data
public class ArticleCreateDTO {

    @NotBlank(message = "标题不能为空")
    @Size(min = 1, max = 200, message = "标题长度1-200")
    private String title;

    @NotBlank(message = "内容不能为空")
    @Size(max = 50000, message = "内容不能超过50000字")
    private String content;

    private Long authorId;
}
