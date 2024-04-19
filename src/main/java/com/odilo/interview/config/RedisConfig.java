package com.odilo.interview.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.odilo.interview.model.UserEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean("redisTemplate")
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setEnableTransactionSupport(true);
        return template;
    }

    @Bean("userRedisTemplate")
    public RedisTemplate<String, UserEntity> userRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, UserEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(createValueSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(createValueSerializer());
        return template;
    }

    private ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    private Jackson2JsonRedisSerializer<UserEntity> createValueSerializer() {
        ObjectMapper objectMapper = createObjectMapper();
        return new Jackson2JsonRedisSerializer<>(objectMapper, UserEntity.class);
    }
}
