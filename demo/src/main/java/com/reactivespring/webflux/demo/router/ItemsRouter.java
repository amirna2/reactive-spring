package com.reactivespring.webflux.demo.router;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactivespring.webflux.demo.handler.ItemsHandler;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler){
        RouterFunction<ServerResponse> rf = RouterFunctions
                .route(GET(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT).and(accept(MediaType.APPLICATION_JSON))
                ,itemsHandler::getAllItems)
                .andRoute(GET(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT + "/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,itemsHandler::getOneItem)
                .andRoute(POST(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT).and(accept(MediaType.APPLICATION_JSON))
                ,itemsHandler::createItem)
                .andRoute(DELETE(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT + "/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,itemsHandler::deleteItem)
                .andRoute(PUT(ItemsHandler.V1_ITEMS_FUNCTIONAL_ENDPOINT + "/{id}").and(accept(MediaType.APPLICATION_JSON))
                ,itemsHandler::updateItem);
        
        return rf;
    }
 
    @Bean
    public RouterFunction<ServerResponse> errorsRoute(ItemsHandler itemsHandler){
        RouterFunction<ServerResponse> rf = RouterFunctions
                .route(GET(ItemsHandler.V1_ERRORS_FUNCTIONAL_ENDPOINT + "/runtimeException" ).and(accept(MediaType.APPLICATION_JSON))
                ,itemsHandler::runtimeException);
        
        return rf;
    }
    
    @Bean
    public RouterFunction<ServerResponse> itemStreamRoute(ItemsHandler itemsHandler){
        RouterFunction<ServerResponse> rf = RouterFunctions
                .route(GET(ItemsHandler.V1_STREAM_ITEMS_FUNCTIONAL_ENDPOINT).and(accept(MediaType.APPLICATION_JSON))
                ,itemsHandler::itemsStream);
        
        return rf;
    }
}
