package com.example.ScallingApi.config;

import com.example.ScallingApi.util.PaginationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

@Component("pageBasedCacheResolver")
public class PageBasedCacheResolver implements CacheResolver {

    private final CacheManager cacheManager;

    @Autowired
    public PageBasedCacheResolver(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Map incoming invocation to one of the predefined caches:
     * - page == 0 -> users-page-0
     * - page == 1 -> users-page-1
     * - page >= 2 -> users-page-rest
     *
     * This avoids creating infinite cache names and keeps predictable TTLs.
     */
    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {

        Object[] args = context.getArgs();

        // Safe extraction: expects single argument PaginationRequest
        if (args == null || args.length == 0 || args[0] == null) {
            Cache c = cacheManager.getCache(RedisConfig.PAGE_REST_CACHE);
            return Collections.singleton(c);
        }

        if (!(args[0] instanceof PaginationRequest)) {
            // fallback — use rest cache
            Cache c = cacheManager.getCache(RedisConfig.PAGE_REST_CACHE);
            return Collections.singleton(c);
        }

        PaginationRequest request = (PaginationRequest) args[0];
        int page = request.getPage() == null ? 0 : request.getPage();

        String cacheName;
        if (page == 0) cacheName = RedisConfig.PAGE_0_CACHE;
        else if (page == 1) cacheName = RedisConfig.PAGE_1_CACHE;
        else cacheName = RedisConfig.PAGE_REST_CACHE;

        Cache cache = cacheManager.getCache(cacheName);
        return Collections.singleton(cache);
    }
}
