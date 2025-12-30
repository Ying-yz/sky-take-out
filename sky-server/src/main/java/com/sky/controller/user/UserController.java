package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserService;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user") // 对应文档路径 /user/user/login
@Api(tags = "C端-用户接口")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 微信登录
     * @param userLoginDTO 包含微信授权码 code
     * @return UserLoginVO 包含 token 等信息
     */
    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("微信用户登录，授权码：{}", userLoginDTO.getCode());

        // 1. 调用 Service 完成微信登录逻辑
        User user = userService.login(userLoginDTO);

        // 2. 为微信用户生成 JWT 令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        
        // 使用 user 专属的 secretKey 和 ttl
        String token = JwtUtil.createJWT(
                jwtProperties.getUserSecretKey(),
                jwtProperties.getUserTtl(),
                claims);

        // 3. 封装返回结果
        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }

    /**
     * 退出登录
     * @return 成功提示
     */
    @PostMapping("/logout")
    @ApiOperation("退出登录")
    public Result logout() {
        log.info("用户退出登录");
        // JWT 是无状态的，后端直接返回成功即可，前端负责清除本地 Token
        return Result.success();
    }
}