package com.dlwlrma.gateway.conf;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * @Author hex1n
 * @Date 2023/8/26/20:27
 * @Description
 **/
@Component
public class RedissonRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<RedissonRateLimiterGatewayFilterFactory.Config> {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(RedissonRateLimiterGatewayFilterFactory.class);

    @Autowired
    private RedissonClient redissonClient;

    public RedissonRateLimiterGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            log.info("start rate limit==============>");
            String token = exchange.getRequest().getHeaders().getFirst("API-Key");
            boolean allowSystem = isAllow("limit-system", 1000, 1, RateIntervalUnit.SECONDS);
            log.info("start rate limit allowSystem=============={}", allowSystem);
            if (!allowSystem) {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
            boolean userSystem = isAllow("limit-" + token, 10, 1, RateIntervalUnit.SECONDS);
            log.info("start rate limit userSystem=============={}",userSystem);
            if (!userSystem) {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
            return chain.filter(exchange);
        };
    }

    public static class Config {

    }

    public boolean isAllow(String key, long permits, long timeout, RateIntervalUnit timeUnit) {
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, permits, timeout, timeUnit);
        return rateLimiter.tryAcquire();
    }
}
