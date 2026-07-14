package com.example.modules.user.repository;

import com.example.modules.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 用户数据访问层
 * 类比前端：类似调用 API 的 service 层，但这里直接操作数据库
 * 
 * JpaRepository 自带：save, findById, findAll, delete 等方法
 * Spring Data JPA 会根据方法名自动生成 SQL（派生查询）
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询 — 方法名自动生成 SQL:
     * SELECT * FROM sys_user WHERE username = ?
     */
    Optional<User> findByUsername(String username);

    /**
     * 检查用户名是否已存在
     */
    boolean existsByUsername(String username);
}
