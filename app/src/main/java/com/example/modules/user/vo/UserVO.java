package com.example.modules.user.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 返回给前端的用户数据结构
 * 
 * ⚠️ 不含 password！这是 VO 存在的意义
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVO {

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private Boolean enabled;
    private LocalDateTime createTime;
}
