package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验拦截器（用户端）
 */
@Component
@Slf4j
public class JwtTokenUserInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 当前拦截的是否为动态方法，若不是则直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        // 1、从请求头中获取令牌，Header 名字对应 yml 中的 user-token-name: authentication
        String token = request.getHeader(jwtProperties.getUserTokenName());

        // 2、校验令牌
        try {
            log.info("jwt校验:{}", token);
            // 使用用户端专属的 SecretKey 进行解析
            Claims claims = JwtUtil.parseJWT(jwtProperties.getUserSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.USER_ID).toString());
            log.info("当前用户id：{}", userId);
            
            // 3、将用户id存入当前线程 ThreadLocal，方便后续 Service 获取
            BaseContext.setCurrentId(userId);
            
            // 4、通过校验，放行
            return true;
        } catch (Exception ex) {
            // 4、校验不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }
}