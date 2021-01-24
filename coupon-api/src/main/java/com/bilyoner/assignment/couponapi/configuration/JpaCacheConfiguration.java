package com.bilyoner.assignment.couponapi.configuration;

import java.util.Arrays;
import java.util.Collections;

import org.aspectj.weaver.patterns.ArgsAnnotationPointcut;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableCaching
public class JpaCacheConfiguration {

    @Bean
	public CacheManager cacheManager() {
		Cache cache = new ConcurrentMapCache("couponsWithEvents");
        Cache cache2 = new ConcurrentMapCache("allcoupons");

		SimpleCacheManager manager = new SimpleCacheManager();
		manager.setCaches(Arrays.asList(cache,cache2));

		return manager;
	}
    
}
