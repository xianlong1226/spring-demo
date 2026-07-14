package com.example.modules.user.controller;

import com.example.common.result.PageResult;
import com.example.common.result.Result;
import com.example.modules.user.dto.LoginDTO;
import com.example.modules.user.dto.RegisterDTO;
import com.example.modules.user.dto.UserUpdateDTO;
import com.example.modules.user.service.UserService;
import com.example.modules.user.vo.LoginVO;
import com.example.modules.user.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 用户接口层
 * 类比前端：类似 Express 路由，定义 URL 和处理函数
 *
 * ⚠️ Controller 只做三件事：接收参数、调用 Service、封装响应
 * ⚠️ 绝不在 Controller 写业务逻辑！
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ==================== 公开接口（无需登录） ====================

    /**
     * 注册
     * POST /api/auth/register
     */
    @PostMapping("/auth/register")
    public Result<UserVO> register(@RequestBody @Valid RegisterDTO dto) {
        return Result.success(userService.register(dto));
    }

    /**
     * 登录
     * POST /api/auth/login
     */
    @PostMapping("/auth/login")
    public Result<LoginVO> login(@RequestBody @Valid LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    // ==================== 需要登录的接口 ====================

    /**
     * 获取当前用户信息
     * GET /api/users/me
     */
    @GetMapping("/users/me")
    public Result<UserVO> getCurrentUser(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return Result.success(userService.findById(userId));
    }

    /**
     * 获取用户详情
     * GET /api/users/1
     */
    @GetMapping("/users/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(userService.findById(id));
    }

    /**
     * 用户列表（分页）
     * GET /api/users?page=1&size=10
     */
    @GetMapping("/users")
    public Result<PageResult<UserVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.success(userService.list(page, size));
    }

    /**
     * 更新用户信息
     * PUT /api/users/1
     */
    @PutMapping("/users/{id}")
    public Result<UserVO> update(@PathVariable Long id,
                                  @RequestBody @Valid UserUpdateDTO dto) {
        return Result.success(userService.update(id, dto));
    }

    /**
     * 删除用户
     * DELETE /api/users/1
     */
    @DeleteMapping("/users/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return Result.success();
    }
}
