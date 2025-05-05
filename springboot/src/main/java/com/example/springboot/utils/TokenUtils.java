package com.example.springboot.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.springboot.entity.Account;
import com.example.springboot.service.AdminService;
import com.example.springboot.service.UserService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Date;
import java.util.Objects;

@Component
public class TokenUtils {

    @Resource
    AdminService adminService;
    @Resource
    UserService userService;

    static AdminService staticeAdminService;
    static UserService staticeUserService;

    // springboot 工程启动后，会启动这段代码
    @PostConstruct
    public void init() {
        staticeAdminService = adminService;
        staticeUserService = userService;
    }

    /**
     * 生成token
     */
    public static String createToken(String data, String sign) {
        return JWT.create().withAudience(data) // 将 userId-role 保存到token里面，作为负载
                .withExpiresAt(DateUtil.offsetDay(new Date(), 1)) // 一天后token过期
                .sign(Algorithm.HMAC256(sign)); // 以 password 作为 token的密钥，HMAC256算法加密，签名
    }

    /**
     * 获取当前登录的用户
     */
    public static Account getCurrentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = request.getHeader("token");
        if (StrUtil.isBlank(token)) {
            token = request.getParameter("token");
        }
        // 拿到token载荷数据
        String audience = JWT.decode(token).getAudience().getFirst();
        String[] split = audience.split("-");
        String userId = split[0];
        String role = split[1];
        // 根据token解析出来的userId去对应的表查询用户信息
        if ("ADMIN".equals(role)) {
            return staticeAdminService.selectById(userId);
        } else if ("USER".equals(role)) {
            return staticeUserService.selectById(userId);
        }
        return null;
    }
}
