package com.example.modules.user.service;

import com.example.common.exception.BusinessException;
import com.example.common.result.PageResult;
import com.example.modules.user.dto.LoginDTO;
import com.example.modules.user.dto.RegisterDTO;
import com.example.modules.user.dto.UserUpdateDTO;
import com.example.modules.user.entity.User;
import com.example.modules.user.repository.UserRepository;
import com.example.modules.user.vo.LoginVO;
import com.example.modules.user.vo.UserVO;
import com.example.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户业务逻辑层 — 核心代码在这里
 *
 * ⚠️ 铁律：业务逻辑只能写在 Service，不能写在 Controller
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 注册
     */
    @Transactional
    public UserVO register(RegisterDTO dto) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 创建用户
        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))  // ✅ 密码加密
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .build();

        user = userRepository.save(user);
        log.info("用户注册成功, username={}", user.getUsername());

        return toVO(user);
    }

    /**
     * 登录
     */
    public LoginVO login(LoginDTO dto) {
        // 通过用户名查用户
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new BusinessException("用户名或密码错误"));

        // 校验密码
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        if (!user.getEnabled()) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成 JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return LoginVO.builder()
                .token(token)
                .user(toVO(user))
                .build();
    }

    /**
     * 根据 ID 查询用户
     */
    public UserVO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));
        return toVO(user);
    }

    /**
     * 分页查询用户列表
     */
    public PageResult<UserVO> list(int page, int size) {
        Page<User> pageData = userRepository.findAll(
                PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime")));

        List<UserVO> list = pageData.getContent().stream()
                .map(this::toVO)
                .toList();

        return PageResult.of(list, pageData.getTotalElements(), page, size);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public UserVO update(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 只更新非空字段
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        user = userRepository.save(user);
        log.info("用户信息更新, id={}", id);

        return toVO(user);
    }

    /**
     * 删除用户
     */
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException("用户不存在");
        }
        userRepository.deleteById(id);
        log.info("用户删除, id={}", id);
    }

    /**
     * Entity → VO 转换
     * ⚠️ 绝对不要直接返回 Entity 给前端！
     */
    private UserVO toVO(User user) {
        return UserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .enabled(user.getEnabled())
                .createTime(user.getCreateTime())
                .build();
    }
}
