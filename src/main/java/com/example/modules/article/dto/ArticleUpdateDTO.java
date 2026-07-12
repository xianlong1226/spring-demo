package com.example.modules.article.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新文章请求参数
 */
@Data
public class ArticleUpdateDTO {

    @Size(min = 1, max = 200, message = "标题长度1-200")
    private String title;

    @Size(max = 50000, message = "内容不能超过50000字")
    private String content;
}
