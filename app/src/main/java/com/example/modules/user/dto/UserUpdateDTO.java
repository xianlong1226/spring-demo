package com.example.modules.user.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户信息请求参数
 */
@Data
public class UserUpdateDTO {

    @Size(min = 2, max = 50, message = "昵称长度2-50")
    private String nickname;

    private String email;
}
