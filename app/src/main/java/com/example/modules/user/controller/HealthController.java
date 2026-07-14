package com.example.modules.user.controller;

import com.example.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 健康检查 / 首页接口
 * 启动项目后访问 GET /api/health 验证服务是否正常
 */
@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        return Result.success(Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now(),
                "message", "Spring Boot Demo is running! 🎉"
        ));
    }
}
