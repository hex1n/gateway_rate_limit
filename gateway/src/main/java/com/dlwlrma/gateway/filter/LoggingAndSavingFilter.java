package com.dlwlrma.gateway.filter;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class LoggingAndSavingFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAndSavingFilter.class);

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startTime = System.currentTimeMillis();

        return chain.filter(exchange).doFinally(signalType -> {
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;
            ServerHttpRequest request = exchange.getRequest();
            // 获取请求路径和响应时间
            String path = exchange.getRequest().getPath().toString();
            RBlockingQueue<Object> blockingQueue = redissonClient.getBoundedBlockingQueue("log-que");

            // 放入队列中异步消费
            // 保存到数据库

            logger.info("Request to {} took {} ms", path, responseTime);
        });
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
