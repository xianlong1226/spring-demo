package com.example.modules.article.mapper;

import com.example.modules.article.entity.Article;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 文章 Mapper — MyBatis 数据访问层
 * 
 * 对比 JPA 的 Repository：
 * - JPA：方法名自动生成 SQL（findByXxx）
 * - MyBatis：手动写 SQL，更灵活可控
 * 
 * 注解模式：SQL 直接写在接口上，简单查询推荐
 * 复杂查询建议用 XML 映射文件
 */
@Mapper
public interface ArticleMapper {

    @Select("SELECT * FROM article WHERE id = #{id}")
    Article findById(@Param("id") Long id);

    @Select("SELECT * FROM article ORDER BY created_at DESC")
    List<Article> findAll();

    @Select("SELECT * FROM article WHERE author_id = #{authorId} ORDER BY created_at DESC")
    List<Article> findByAuthorId(@Param("authorId") Long authorId);

    @Insert("INSERT INTO article(title, content, author_id, created_at, updated_at) " +
            "VALUES(#{title}, #{content}, #{authorId}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Article article);

    @Update("UPDATE article SET title = #{title}, content = #{content}, updated_at = NOW() " +
            "WHERE id = #{id}")
    int update(Article article);

    @Delete("DELETE FROM article WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
