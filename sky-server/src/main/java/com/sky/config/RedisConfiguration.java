package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建 Redis 模板对象...");
        RedisTemplate redisTemplate = new RedisTemplate();

        // 设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 1. 设置 Key 的序列化器（解决 Key 的乱码）
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 2. 设置 Value 的序列化器（解决“北京”等值的乱码）【新增】
        // 这里建议也用 String，如果你存的是字符串
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // 3. 设置 Hash Key 和 Hash Value 的序列化器（解决购物车等哈希结构的乱码）【新增】
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}