package com.trading.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public GlobalFilter requestIdFilter() {
        return (exchange, chain) -> {
            var req = exchange.getRequest();

            String ridHeader = req.getHeaders().getFirst("X-Request-ID");
            final String requestId = (ridHeader != null && !ridHeader.isBlank())
                    ? ridHeader
                    : UUID.randomUUID().toString();

            ServerHttpRequest mutated = req.mutate()
                    .header("X-Request-ID", requestId)
                    .build();

            return chain.filter(exchange.mutate().request(mutated).build())
                    .then(Mono.fromRunnable(() ->
                            exchange.getResponse().getHeaders().set("X-Request-ID", requestId)
                    ));
        };
    }
}
