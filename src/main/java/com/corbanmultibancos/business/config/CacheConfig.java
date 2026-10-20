package com.corbanmultibancos.business.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;

@Configuration
@EnableCaching
@Profile({"prod","dev"})
public class CacheConfig {
    public static final String BANK_ID = "bank-id";
    public static final String BANK_LIST = "bank-list";
    public static final String TEAM_ID = "team-id";
    public static final String TEAM_LIST = "team-list";
    public static final String REPORT = "report";

    @Bean
    RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory, ObjectMapper objectMapper) {
        RedisCacheConfiguration defaultCache = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(10))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new JdkSerializationRedisSerializer()));
        RedisCacheConfiguration bankCache = defaultCache.entryTtl(Duration.ofHours(12));
        RedisCacheConfiguration teamCache = defaultCache.entryTtl(Duration.ofHours(12));
        RedisCacheConfiguration reportCache = defaultCache.entryTtl(Duration.ofMinutes(1));
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCache)
                .withCacheConfiguration(BANK_ID, bankCache)
                .withCacheConfiguration(BANK_LIST, bankCache)
                .withCacheConfiguration(TEAM_ID, teamCache)
                .withCacheConfiguration(TEAM_LIST, teamCache)
                .withCacheConfiguration(REPORT, reportCache)
                .build();
    }

    private GenericJacksonJsonRedisSerializer buildJsonSerializer(ObjectMapper objectMapper) {
        var typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType("com.corbanmultibancos.business.dto.")
                .allowIfSubType("java.util.")
                .build();
        ObjectMapper cacheObjectMapper = objectMapper.rebuild()
                .activateDefaultTypingAsProperty(typeValidator, DefaultTyping.NON_FINAL, "@class")
                .build();
        return new GenericJacksonJsonRedisSerializer(cacheObjectMapper);
    }
}
