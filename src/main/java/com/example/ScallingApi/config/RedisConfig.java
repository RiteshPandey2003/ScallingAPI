package com.example.ScallingApi.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    // Cache names (single source of truth)
    public static final String PAGE_0_CACHE      = "users-page-0";
    public static final String PAGE_1_CACHE      = "users-page-1";
    public static final String PAGE_REST_CACHE   = "users-page-rest";

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // Default local lettuce connection (adjust host/port if needed via application.properties)
        return new LettuceConnectionFactory("localhost", 6379);
    }

    /**
     * Jackson-based value serializer (configured once).
     * Using Jackson2JsonRedisSerializer with a polymorphic validator so cached JSON contains
     * type information (prevents LinkedHashMap casting).
     */
    @Bean
    public RedisSerializer<Object> redisValueSerializer() {
        ObjectMapper mapper = new ObjectMapper();
        // handle java.time.* types
        mapper.registerModule(new JavaTimeModule());

        // Polymorphic type handling: restrict allowed base packages if you want higher security.
        BasicPolymorphicTypeValidator ptv =
                BasicPolymorphicTypeValidator.builder()
                        .allowIfBaseType("com.example.ScallingApi") // limit to your base package
                        .allowIfBaseType(Object.class) // keep Object allowed if you need broad types; tighten if required
                        .build();

        // Attach polymorphic type info to enable correct deserialization into your types
        mapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        Jackson2JsonRedisSerializer<Object> ser = new Jackson2JsonRedisSerializer<>(Object.class);
        ser.setObjectMapper(mapper);
        return ser;
    }

    /**
     * Create a RedisCacheManager and register a small, fixed set of caches with per-cache TTLs.
     * This avoids creating caches dynamically per page at runtime and is efficient.
     */
    @Bean
    @Primary
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory,
                                          RedisSerializer<Object> redisValueSerializer) {

        // key serializer
        RedisSerializer<String> keySerializer = new StringRedisSerializer();

        // default config (fallback)
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisValueSerializer))
                .entryTtl(Duration.ofMinutes(10)) // fallback TTL
                .disableCachingNullValues();

        // per-cache configs (create once here)
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        cacheConfigurations.put(PAGE_0_CACHE,
                defaultConfig.entryTtl(Duration.ofSeconds(30)));

        cacheConfigurations.put(PAGE_1_CACHE,
                defaultConfig.entryTtl(Duration.ofMinutes(1)));

        cacheConfigurations.put(PAGE_REST_CACHE,
                defaultConfig.entryTtl(Duration.ofMinutes(10)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
