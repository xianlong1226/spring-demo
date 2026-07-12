package com.example.modules.article.service;

import com.example.common.exception.BusinessException;
import com.example.common.result.PageResult;
import com.example.modules.article.dto.ArticleCreateDTO;
import com.example.modules.article.dto.ArticleUpdateDTO;
import com.example.modules.article.entity.Article;
import com.example.modules.article.mapper.ArticleMapper;
import com.example.modules.article.vo.ArticleVO;
import com.example.modules.user.entity.User;
import com.example.modules.user.repository.UserRepository;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 文章业务逻辑层 — 用 MyBatis 实现
 * 
 * 对比 UserService（JPA）：
 * - JPA：userRepository.findById(id)，方法名即 SQL
 * - MyBatis：articleMapper.findById(id)，手写 SQL 更灵活
 * 
 * 分页对比：
 * - JPA：PageRequest.of(page, size)，返回 Page<T>
 * - MyBatis + PageHelper：PageHelper.startPage(page, size)，拦截 SQL 自动加分页
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleMapper articleMapper;
    private final UserRepository userRepository;

    /**
     * 创建文章
     */
    @Transactional
    public ArticleVO create(ArticleCreateDTO dto) {
        // 如果没传 authorId，默认用 1（简化处理，实际应从登录态获取）
        Long authorId = dto.getAuthorId() != null ? dto.getAuthorId() : 1L;

        // 校验作者是否存在
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new BusinessException("作者不存在"));

        Article article = Article.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .authorId(authorId)
                .build();

        articleMapper.insert(article);
        log.info("文章创建成功, id={}, title={}", article.getId(), article.getTitle());

        return toVO(article, author);
    }

    /**
     * 根据 ID 查询文章
     */
    public ArticleVO findById(Long id) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        String authorName = userRepository.findById(article.getAuthorId())
                .map(User::getNickname)
                .orElse("未知作者");

        return toVO(article, authorName);
    }

    /**
     * 分页查询文章列表
     * 
     * PageHelper 原理：
     * 1. PageHelper.startPage() 设置分页参数到 ThreadLocal
     * 2. 紧接着的第一次查询会被拦截，自动拼接 LIMIT/OFFSET
     * 3. 查询后自动清除分页参数
     */
    public PageResult<ArticleVO> list(int page, int size) {
        PageHelper.startPage(page, size);
        List<Article> articles = articleMapper.findAll();

        PageInfo<Article> pageInfo = new PageInfo<>(articles);

        List<ArticleVO> voList = articles.stream()
                .map(article -> {
                    String authorName = userRepository.findById(article.getAuthorId())
                            .map(User::getNickname)
                            .orElse("未知作者");
                    return toVO(article, authorName);
                })
                .toList();

        return PageResult.of(voList, pageInfo.getTotal(), page, size);
    }

    /**
     * 更新文章
     */
    @Transactional
    public ArticleVO update(Long id, ArticleUpdateDTO dto) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 只更新非空字段
        if (dto.getTitle() != null) {
            article.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            article.setContent(dto.getContent());
        }

        articleMapper.update(article);
        log.info("文章更新, id={}", id);

        return findById(id);
    }

    /**
     * 删除文章
     */
    @Transactional
    public void delete(Long id) {
        Article article = articleMapper.findById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        articleMapper.deleteById(id);
        log.info("文章删除, id={}", id);
    }

    /**
     * Entity → VO 转换
     */
    private ArticleVO toVO(Article article, User author) {
        return ArticleVO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .authorId(article.getAuthorId())
                .authorName(author != null ? author.getNickname() : "未知作者")
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }

    private ArticleVO toVO(Article article, String authorName) {
        return ArticleVO.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .authorId(article.getAuthorId())
                .authorName(authorName)
                .createdAt(article.getCreatedAt())
                .updatedAt(article.getUpdatedAt())
                .build();
    }
}
