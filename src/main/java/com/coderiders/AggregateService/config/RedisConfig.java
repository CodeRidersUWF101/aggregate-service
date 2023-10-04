package com.coderiders.AggregateService.config;

import com.coderiders.commonutils.models.UserLibraryWithBookDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, UserLibraryWithBookDetails> redisTemplate(RedisConnectionFactory factory) {
        Jackson2JsonRedisSerializer<UserLibraryWithBookDetails> serializer = new Jackson2JsonRedisSerializer<>(UserLibraryWithBookDetails.class);

        RedisTemplate<String, UserLibraryWithBookDetails> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(serializer);
        template.setKeySerializer(new StringRedisSerializer());
        return template;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(60));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(config)
                .build();
    }
}
