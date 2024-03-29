package com.reactivespring.webflux.demo.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactivespring.webflux.demo.handler.SampleHandlerFunction;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> route(SampleHandlerFunction handlerFunction){
        RouterFunction<ServerResponse> rf = RouterFunctions
                .route(GET("/functional/flux").and(accept(MediaType.APPLICATION_JSON)),handlerFunction::flux)
                .andRoute(GET("/functional/mono").and(accept(MediaType.APPLICATION_JSON)),handlerFunction::mono);
        return rf;

    }
}
