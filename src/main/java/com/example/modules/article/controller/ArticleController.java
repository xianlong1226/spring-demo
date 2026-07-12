package com.example.modules.article.controller;

import com.example.common.result.PageResult;
import com.example.common.result.Result;
import com.example.modules.article.dto.ArticleCreateDTO;
import com.example.modules.article.dto.ArticleUpdateDTO;
import com.example.modules.article.service.ArticleService;
import com.example.modules.article.vo.ArticleVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 文章接口层 — MyBatis 实现
 * 
 * 对比 UserController（JPA）：
 * - Controller 层写法完全一样，差别在底层数据访问方式
 * - JPA 用 Repository，MyBatis 用 Mapper
 * - Controller 不关心底层用什么，只调 Service
 */
@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 创建文章
     * POST /api/articles
     */
    @PostMapping
    public Result<ArticleVO> create(@RequestBody @Valid ArticleCreateDTO dto,
                                     Authentication auth) {
        // 从登录态获取作者 ID（如果已登录）
        if (auth != null && dto.getAuthorId() == null) {
            dto.setAuthorId((Long) auth.getPrincipal());
        }
        return Result.success(articleService.create(dto));
    }

    /**
     * 获取文章详情
     * GET /api/articles/1
     */
    @GetMapping("/{id}")
    public Result<ArticleVO> getById(@PathVariable Long id) {
        return Result.success(articleService.findById(id));
    }

    /**
     * 文章列表（分页）
     * GET /api/articles?page=1&size=10
     */
    @GetMapping
    public Result<PageResult<ArticleVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(articleService.list(page, size));
    }

    /**
     * 更新文章
     * PUT /api/articles/1
     */
    @PutMapping("/{id}")
    public Result<ArticleVO> update(@PathVariable Long id,
                                     @RequestBody @Valid ArticleUpdateDTO dto) {
        return Result.success(articleService.update(id, dto));
    }

    /**
     * 删除文章
     * DELETE /api/articles/1
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        articleService.delete(id);
        return Result.success();
    }
}
