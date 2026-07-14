package com.example.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AOP 日志切面 — 自动记录 Service 方法调用信息
 * 
 * 前端类比：类似 axios interceptor，但作用在后端 Service 层
 * 不需要每个方法手写日志，切面自动拦截并记录
 */
@Slf4j
@Aspect
@Component
public class LogAspect {

    /**
     * 敏感字段关键词，匹配到的字段值会脱敏为 ***
     */
    private static final List<String> SENSITIVE_KEYS = List.of("password", "pwd", "secret", "token");

    /**
     * 拦截 modules 包下所有 Service 的所有方法
     */
    @Around("execution(* com.example.modules..service..*.*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String params = sanitizeParams(joinPoint.getArgs());

        log.info(">>> {}.{}() 参数: {}", className, methodName, params);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - startTime;
            log.info("<<< {}.{}() 耗时: {}ms", className, methodName, elapsed);
            return result;
        } catch (Throwable e) {
            long elapsed = System.currentTimeMillis() - startTime;
            log.error("!!! {}.{}() 异常: {} | 耗时: {}ms", className, methodName, e.getMessage(), elapsed);
            throw e;
        }
    }

    /**
     * 参数脱敏：含敏感关键词的字段值替换为 ***
     */
    private String sanitizeParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) return "null";
                    String str = arg.toString();
                    for (String key : SENSITIVE_KEYS) {
                        // 简单脱敏：如果 toString 中包含 password=xxx 的模式
                        str = str.replaceAll("(?i)(" + key + "=)([^,}\\]]+)", "$1***");
                    }
                    // 避免 toString 过长
                    if (str.length() > 200) {
                        str = str.substring(0, 200) + "...";
                    }
                    return str;
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
