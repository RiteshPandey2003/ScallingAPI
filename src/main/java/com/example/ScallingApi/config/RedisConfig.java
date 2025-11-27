package com.example.ScallingApi.config;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

//
//@Configuration
//@EnableCaching
//public class RedisConfig {
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//
//        // KEEP THIS — required for generics like PagingResult<User>
//        mapper.activateDefaultTyping(
//                BasicPolymorphicTypeValidator.builder()
//                        .allowIfBaseType(Object.class)
//                        .build(),
//                ObjectMapper.DefaultTyping.NON_FINAL
//        );
//
//        Jackson2JsonRedisSerializer<Object> serializer =
//                new Jackson2JsonRedisSerializer<>(mapper, Object.class);
//
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(connectionFactory);
//
//        template.setKeySerializer(new StringRedisSerializer());
//        template.setValueSerializer(serializer);
//
//        template.setHashKeySerializer(new StringRedisSerializer());
//        template.setHashValueSerializer(serializer);
//
//        return template;
//    }
//
//    @Bean
//    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule());
//
//        mapper.activateDefaultTyping(
//                BasicPolymorphicTypeValidator.builder()
//                        .allowIfBaseType(Object.class)
//                        .build(),
//                ObjectMapper.DefaultTyping.NON_FINAL
//        );
//
//        Jackson2JsonRedisSerializer<Object> serializer =
//                new Jackson2JsonRedisSerializer<>(mapper, Object.class);
//
//        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
//                .serializeKeysWith(
//                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .serializeValuesWith(
//                        RedisSerializationContext.SerializationPair.fromSerializer(serializer)
//                )
//                .entryTtl(Duration.ofMinutes(10));
//
//        return RedisCacheManager.builder(connectionFactory)
//                .cacheDefaults(config)
//                .build();
//    }
//}


@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {

        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(Object.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        serializer.setObjectMapper(mapper);

        RedisCacheConfiguration defaultConfig =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .serializeKeysWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        new StringRedisSerializer()
                                ))
                        .serializeValuesWith(
                                RedisSerializationContext.SerializationPair.fromSerializer(
                                        serializer
                                ));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .build();
    }
}