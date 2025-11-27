package com.example.ScallingApi.config;

import com.example.ScallingApi.util.PaginationRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Component;





import java.time.Duration;
import java.util.Collection;
import java.util.Collections;

@Component
public class PageBasedCacheResolver implements CacheResolver {

    @Autowired
    private RedisConnectionFactory connectionFactory;

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {

        Object[] params = context.getArgs();

        // ✔ Your controller: getAllUsers(PaginationRequest request)

        PaginationRequest request = (PaginationRequest) params[0];

        int page = request.getPage(); // Extract page safely

        Duration ttl;

        // ✔ TTL rules
        if (page == 0) ttl = Duration.ofSeconds(30);
        else if (page == 1) ttl = Duration.ofMinutes(1);
        else ttl = Duration.ofMinutes(10);

        String cacheName = "users-page-" + page;

        // ✔ Build per-page TTL cache configuration
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(ttl)
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer())
                );

        // ✔ Create cache manager only for this cache (allowed)
        RedisCacheManager manager = RedisCacheManager
                .builder(connectionFactory)
                .cacheDefaults(config)
                .build();

        // ✔ Return specific page cache only
        return Collections.singleton(manager.getCache(cacheName));
    }
}
